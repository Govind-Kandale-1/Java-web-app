pipeline {
    agent any

    tools {
        maven 'mvn'             // Your configured Maven installation
        jdk 'OpenJDK-17'        // Your configured JDK installation
    }

    environment {
        DOCKER_COMPOSE_DIR = 'simple-java-app'  // Path to docker-compose.yml
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                bat 'dir'
                bat "cd ${env.DOCKER_COMPOSE_DIR} && mvn clean test"
            }
        }

        stage('Build and Run Docker') {
            steps {
                bat "cd ${env.DOCKER_COMPOSE_DIR} && docker-compose build"
                bat "cd ${env.DOCKER_COMPOSE_DIR} && docker-compose up -d"
            }
        }

        // Optionally add a sleep and health check here or test API endpoint

        stage('Stop Docker Containers') {
            steps {
                bat "cd ${env.DOCKER_COMPOSE_DIR} && docker-compose down"
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
            cleanWs()
        }
    }
}
