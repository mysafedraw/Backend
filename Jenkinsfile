pipeline {
    agent any

    environment {
        ECR_REPO = '575108921650.dkr.ecr.ap-northeast-2.amazonaws.com/mysafedraw/backend-image'
        AWS_REGION = 'ap-northeast-2'
        K8S_BRANCH = 'k8s' // Kubernetes 배포 파일 브랜치
        CREDENTIALS_ID = 'aws-credentials-id'
    }

    stages {
        stage('Copy YML File for Test') {
            steps {
                script {
                    dir('backend') {
                        withCredentials([file(credentialsId: 'test-application-yml', variable: 'TEST_APPLICATION_YML')]) {
                            sh 'cp $TEST_APPLICATION_YML ./src/main/resources/application.yml'
                        }
                    }
                }
            }
        }

        stage('Build Spring Boot App for Test') {
            steps {
                script {
                    docker.image('eclipse-temurin:17-jdk-alpine').inside {
                        dir('backend') {
                            sh 'chmod +x gradlew' // 실행 권한 부여
                            sh './gradlew clean build --stacktrace -x test'
                        }
                    }
                }
            }
        }

        stage('Deploy with Docker Compose for Test') {
            steps {
                script {
                    dir('backend') {
                        sh 'docker-compose up --build -d'
                    }
                }
            }
        }

        stage('Copy YML File for Prod') {
            steps {
                script {
                    dir('backend') {
                        withCredentials([file(credentialsId: 'prod-application-yml', variable: 'PROD_APPLICATION_YML')]) {
                            sh 'cp -f $PROD_APPLICATION_YML ./src/main/resources/application.yml'
                        }
                    }
                }
            }
        }

        stage('Build Spring Boot App for Prod') {
            steps {
                script {
                    docker.image('eclipse-temurin:17-jdk-alpine').inside {
                        dir('backend') {
                            sh 'chmod +x gradlew' // 실행 권한 부여
                            sh './gradlew clean build --stacktrace -x test'
                        }
                    }
                }
            }
        }

        stage('Build Docker Image for Prod') {
            steps {
                script {
                    echo "Building Docker image with tag: ${env.BUILD_NUMBER}..."
                    dir('backend') {
                        sh """
                        docker build -t ${ECR_REPO}:${env.BUILD_NUMBER} .
                        docker tag ${ECR_REPO}:${env.BUILD_NUMBER} ${ECR_REPO}:latest
                        """
                    }
                }
            }
        }

        stage('AWS CLI Login') {
            steps {
                withAWS(credentials: CREDENTIALS_ID, region: AWS_REGION) {
                    sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
                    """
                }
            }
        }

        stage('Push Docker Image to AWS ECR') {
            steps {
                script {
                    echo "Pushing Docker image to ECR..."
                    dir('backend') {
                        sh """
                        docker push ${ECR_REPO}:${env.BUILD_NUMBER}
                        docker push ${ECR_REPO}:latest
                        """
                    }
                }
            }
        }

        stage('Clean Up Docker Images') {
            steps {
                script {
                    echo "Cleaning up Docker images..."
                    sh 'docker image prune -f'
                }
            }
        }

        stage('Switch to K8S Branch') {
            steps {
                script {
                    echo "Switching to K8S branch..."
                    withCredentials([usernamePassword(
                        credentialsId: 'gitlab-credentials', 
                        passwordVariable: 'GIT_PASSWORD', 
                        usernameVariable: 'GIT_USERNAME'
                    )]) {
                        sh '''
                        # Git 인증 URL 설정
                        git remote set-url origin https://${GIT_USERNAME}:${GIT_PASSWORD}@lab.ssafy.com/s11-final/S11P31A405.git
                        # 로컬 변경 사항 임시 저장
                        git add .
                        git stash
                        git fetch origin
                        git checkout ${K8S_BRANCH}
                        git pull origin ${K8S_BRANCH}
                        # 임시 저장된 변경 사항 복원
                        git stash pop || echo "No stash to apply"
                        '''
                    }
                }
            }
        }

        stage('Update K8S Deployment File') {
            steps {
                script {
                    echo "Updating backend-deployment.yaml with new image tag: ${env.BUILD_NUMBER}"
                    sh """
                    sed -i 's|image: .*|image: ${ECR_REPO}:${env.BUILD_NUMBER}|' k8s/backend/backend-deployment.yaml
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
                    git commit -m "Update backend image to ${env.BUILD_NUMBER}"
                    git push origin ${K8S_BRANCH}
                    """
                }
            }
        }
    }
}