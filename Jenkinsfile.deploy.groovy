pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                setBuildStatus("?", "PENDING")
                withMaven(
                        maven: 'maven-latest'
                ) {
                    sh 'mvn -B  clean install'
                }
            }
        }
        stage('Deliver') {
            environment {
                DATA = '/var/opex/chain-scan'
            }
            steps {
                dir("Deployment") {
                    sh 'docker-compose up -d --build'
                }
            }
        }
    }

    post {
        always {
            echo 'One way or another, I have finished'
        }
        success {
            echo ':)'
            setBuildStatus(":)", "SUCCESS")
        }
        unstable {
            echo ':/'
            setBuildStatus(":/", "UNSTABLE")
        }
        failure {
            echo ':('
            setBuildStatus(":(", "FAILURE")
        }
        changed {
            echo 'Things were different before...'
        }
    }
}

void setBuildStatus(String message, String state) {
    step([
            $class            : "GitHubCommitStatusSetter",
            reposSource       : [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/opexdev/Chain-Scan"],
            contextSource     : [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
            errorHandlers     : [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
            statusResultSource: [$class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]]]
    ])
}
