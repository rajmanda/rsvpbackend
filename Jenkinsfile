pipeline {
    agent any

//     environment {
//         JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
//         MAVEN_HOME = '/usr/share/maven'
//         PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
//     }

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
               sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
               sh 'docker build -t codedecode25/restaurant-listing-service:${VERSION} .'
               sh 'docker push codedecode25/restaurant-listing-service:${VERSION}'
           }
         }

          stage('Cleanup Workspace') {
           steps {
             deleteDir()
           }
    }
}
