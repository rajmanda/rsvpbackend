pipeline {
    agent any

    environment {
        GIT_CREDENTIALS = credentials('GIT_CREDENTIALS')
    }

    stages {
        stage('Clone repository') {
            steps {
                // Securely pass credentialsId without interpolation
                git url: 'git@github.com:rajmanda/rsvpbackend.git', credentialsId: 'GIT_CREDENTIALS'
            }
        }
        stage('Build') {
            steps {
                // Run Maven clean and package
                sh 'mvn clean package'
            }
        }
    }
}
