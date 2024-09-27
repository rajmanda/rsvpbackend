pipeline {
    any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
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
                sh 'echo $JAVA_HOME'
                sh 'java -version'
                sh 'mvn -version'
                // Ensure JAVA_HOME is used by Maven
                sh 'JAVA_HOME=$JAVA_HOME mvn clean package'
            }
        }
    }
}
