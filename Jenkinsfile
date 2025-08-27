pipeline {
    agent any

    tools {
        maven 'Maven 3.8.6' // Your configured Maven name in Jenkins
        jdk 'JDK 17'        // Your configured JDK name in Jenkins
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
