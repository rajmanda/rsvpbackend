pipeline {
    agent any

    environment {
        VERSION = 'latest' // Ensure this is defined or passed in from the build parameters
    }

    stages {
        stage('Clone repository') {
            steps {
                // Securely pass credentialsId without interpolation
                git url: 'git@github.com:rajmanda/rsvpbackend.git', branch: 'main', credentialsId: 'GIT_CREDENTIALS'
            }
        }

        stage('Check Environment') {
            steps {
                sh 'echo $JAVA_HOME'
                sh 'java -version'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Docker Build and Push') {
            steps {
                // DockerHub credentials should be securely managed with Jenkins credentials
                withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CREDENTIALS', usernameVariable: 'DOCKERHUB_CREDENTIALS_USR', passwordVariable: 'DOCKERHUB_CREDENTIALS_PSW')]) {
                    sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                    // Update image name to match your Docker Hub repository
                    sh 'docker build -t dockerrajmanda/rsvpbackend:${VERSION} .'
                    sh 'docker push dockerrajmanda/rsvpbackend:${VERSION}'
                }
            }
        }

        stage('Cleanup Workspace') {
            steps {
                deleteDir()
            }
        }
    }
}
