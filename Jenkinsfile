pipeline {
    agent any

    environment {
        ECR_REPO = '575108921650.dkr.ecr.ap-northeast-2.amazonaws.com/mysafedraw/backend-image'
        AWS_REGION = 'ap-northeast-2'
        K8S_BRANCH = 'k8s' // Kubernetes 배포 파일 브랜치
        CREDENTIALS_ID = 'aws-credentials-id'
    }

    stages {
        stage('Set GIT COMMIT SHA') {
            steps {
                script {
                    env.GIT_COMMIT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    echo "Current Git Commit: ${GIT_COMMIT}"
                }
            }
        }

        // stage('Copy YML File for Test') {
        //     steps {
        //         script {
        //             dir('backend') {
        //                 withCredentials([file(credentialsId: 'application-yml', variable: 'APPLICATION_YML')]) {
        //                     sh 'cp $APPLICATION_YML ./src/main/resources/application.yml'
        //                 }
        //             }
        //         }
        //     }
        // }

        // stage('Build Spring Boot App for Test') {
        //     steps {
        //         script {
        //             docker.image('eclipse-temurin:17-jdk-alpine').inside {
        //                 dir('backend') {
        //                     sh 'chmod +x gradlew' // 실행 권한 부여
        //                     sh './gradlew clean build --stacktrace -x test'
        //                 }
        //             }
        //         }
        //     }
        // }

        // stage('Deploy with Docker Compose for Test') {
        //     steps {
        //         script {
        //             dir('backend') {
        //                 sh 'docker-compose up --build -d'
        //             }
        //         }
        //     }
        // }

        // stage('Copy YML File for Prod') {
        //     steps {
        //         script {
        //             dir('backend') {
        //                 withCredentials([file(credentialsId: 'prod-application-yml', variable: 'PROD_APPLICATION_YML')]) {
        //                     sh 'cp -f $PROD_APPLICATION_YML ./src/main/resources/application.yml'
        //                 }
        //             }
        //         }
        //     }
        // }

        // stage('Build Spring Boot App for Prod') {
        //     steps {
        //         script {
        //             docker.image('eclipse-temurin:17-jdk-alpine').inside {
        //                 dir('backend') {
        //                     sh 'chmod +x gradlew' // 실행 권한 부여
        //                     sh './gradlew clean build --stacktrace -x test'
        //                 }
        //             }
        //         }
        //     }
        // }

        // stage('Build Docker Image for Prod') {
        //     steps {
        //         script {
        //             echo "Building Docker image with tag: ${GIT_COMMIT}..."
        //             dir('backend') {
        //                 sh """
        //                 docker build -t ${ECR_REPO}:${GIT_COMMIT} .
        //                 """
        //             }
        //         }
        //     }
        // }

        // stage('AWS CLI Login') {
        //     steps {
        //         withAWS(credentials: CREDENTIALS_ID, region: AWS_REGION) {
        //             sh """
        //             aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
        //             """
        //         }
        //     }
        // }

        // stage('Push Docker Image to AWS ECR') {
        //     steps {
        //         script {
        //             echo "Pushing Docker image to ECR..."
        //             dir('backend') {
        //                 sh """
        //                 docker push ${ECR_REPO}:${GIT_COMMIT}
        //                 docker tag ${ECR_REPO}:${GIT_COMMIT} ${ECR_REPO}:latest
        //                 docker push ${ECR_REPO}:latest
        //                 """
        //             }
        //         }
        //     }
        // }

        // stage('Clean Up Docker Images') {
        //     steps {
        //         script {
        //             echo "Cleaning up Docker images..."
        //             sh 'docker image prune -f'
        //         }
        //     }
        // }

        stage('Switch to K8S Branch') {
            steps {
                script {
                    echo "Switching to K8S branch..."
                    withCredentials([usernamePassword(credentialsId: '7625e1be-1711-422c-a591-689d805d2f75', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
                        sh '''
                        git fetch https://${GIT_USERNAME}:${GIT_PASSWORD}@lab.ssafy.com/s11-final/S11P31A405.git
                        git checkout ${K8S_BRANCH}
                        git pull origin ${K8S_BRANCH}
                        '''
                    }
                }
            }
        }

        stage('Update K8S Deployment File') {
            steps {
                script {
                    echo "Updating backend-deployment.yaml with new image tag: ${GIT_COMMIT}"
                    sh """
                    sed -i 's|image: .*|image: ${ECR_REPO}:${GIT_COMMIT}|' k8s/backend/backend-deployment.yaml
                    git status
                    """
                }
            }
        }

        stage('Commit and Push Changes to K8S Branch') {
            steps {
                script {
                    echo "Committing changes to ${K8S_BRANCH} branch..."
                    sh """
                    git config user.name "Jenkins"
                    git config user.email "jenkins@example.com"
                    git add k8s/backend/backend-deployment.yaml
                    git commit -m "Update backend image to ${GIT_COMMIT}"
                    git push origin ${K8S_BRANCH}
                    """
                }
            }
        }
    }
}