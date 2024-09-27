pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                // Run Maven clean and package
                sh 'mvn clean package'
            }
        }
    }
}
