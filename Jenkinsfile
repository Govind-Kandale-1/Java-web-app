pipeline {
    agent any

    tools {
        maven 'mvn' // Your configured Maven name in Jenkins
        jdk 'OpenJDK-17'        // Your configured JDK name in Jenkins
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }
    }

    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            cleanWs()
        }
    }
}
