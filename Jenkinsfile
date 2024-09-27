pipeline {
    agent any

    environment {
        GIT_CREDENTIALS = credentials('GIT_CREDENTIALS')
    }

    stages {
        stages {
            stage('Clone repository') {
                steps {
                    git url: 'git@github.com:your-user/your-repo.git', credentialsId: "${GIT_CREDENTIALS}"
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
}
