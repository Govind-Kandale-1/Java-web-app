pipeline {
    agent any

    tools {
        maven 'mvn'
        jdk   'OpenJDK-17'
    }

    environment {
        APP_DIR        = 'simple-java-app'
        IMAGE_NAME     = 'simple-java-app'
        IMAGE_TAG      = "${env.BUILD_NUMBER}"
        CONTAINER_PORT = '8082'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                dir(env.APP_DIR) {
                    bat 'mvn clean verify -B'
                }
            }
            post {
                always {
                    junit allowEmptyResults: false,
                          testResults: "${env.APP_DIR}/target/surefire-reports/*.xml"
                    jacoco(
                        execPattern:   "${env.APP_DIR}/target/jacoco.exec",
                        classPattern:  "${env.APP_DIR}/target/classes",
                        sourcePattern: "${env.APP_DIR}/src/main/java",
                        minimumLineCoverage: '60'
                    )
                }
            }
        }

        stage('Docker Build') {
            steps {
                dir(env.APP_DIR) {
                    bat "docker build -t ${env.IMAGE_NAME}:${env.IMAGE_TAG} -t ${env.IMAGE_NAME}:latest ."
                }
            }
        }

        stage('Docker Run') {
            steps {
                bat "docker-compose -f ${env.APP_DIR}/docker-compose.yaml up -d"
            }
        }

        stage('Health Check') {
            steps {
                bat '''
                    @echo off
                    set RETRIES=10
                    set DELAY=6
                    :loop
                    curl -sf http://localhost:%CONTAINER_PORT%/api/health
                    if %ERRORLEVEL%==0 (
                        echo Health check passed.
                        exit /b 0
                    )
                    set /a RETRIES-=1
                    if %RETRIES%==0 (
                        echo Health check failed after all retries.
                        exit /b 1
                    )
                    timeout /t %DELAY% /nobreak >nul
                    goto loop
                '''
            }
        }
    }

    post {
        always {
            bat "docker-compose -f ${env.APP_DIR}/docker-compose.yaml down --remove-orphans"
            cleanWs()
        }
        success {
            echo "Build ${env.BUILD_NUMBER} succeeded. Image: ${env.IMAGE_NAME}:${env.IMAGE_TAG}"
        }
        failure {
            echo "Build ${env.BUILD_NUMBER} failed. Check test results and logs above."
        }
    }
}
