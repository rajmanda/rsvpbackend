pipeline {
    agent any

    environment {
        GIT_CREDENTIALS = credentials('GIT_CREDENTIALS')
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'     // Ensure this matches the Dockerfile
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"  // Ensure Maven and Java are in PATH

    }

    stages {
        stage('Clone repository') {
            steps {
                // Securely pass credentialsId without interpolation
                // git url: 'git@github.com:rajmanda/rsvpbackend.git', credentialsId: 'GIT_CREDENTIALS'
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
                // Run Maven clean and package
                sh 'mvn clean package'
            }
        }
    }
}
