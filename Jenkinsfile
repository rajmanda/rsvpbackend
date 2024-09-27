pipeline {
    agent any

    environment {
        GIT_CREDENTIALS = credentials('GITHUB_SSH_CREDENTIAL')
    }

    stages {
        stages {
            stage('Clone repository') {
                steps {
                    git url: 'git@github.com:your-user/your-repo.git', credentialsId: "${GIT_CREDENTIALS}"
                }
            }
        }

//         stage('Build') {
//             steps {
//                 // Run Maven clean and package
//                 sh 'mvn clean package'
//             }
//         }
    }
}
