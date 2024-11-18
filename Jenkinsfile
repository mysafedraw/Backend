pipeline {
    agent any

    environment {
        CI = 'false'
        GIT_COMMIT = '' // 전역 변수로 설정
    }

    stages {
        stage('Set GIT_COMMIT') {
            steps {
                script {
                    // 현재 Git 커밋 SHA를 전역 변수에 저장
                    GIT_COMMIT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    echo "Current Git Commit: ${GIT_COMMIT}"
                }
            }
        }

        stage('yml file copy for Test') {
            steps {
                script {
                    dir('backend') {
                        withCredentials([file(credentialsId: 'application-yml', variable: 'APPLICATION_YML')]) {
                            sh '''
                            cp $APPLICATION_YML ./src/main/resources/application.yml
                            '''
                        }
                    }
                }
            }
        }

        stage('Build Spring Boot App for Test') {
            steps {
                script {
                    echo 'Building Spring Boot app for testing...'
                    docker.image('eclipse-temurin:17-jdk-alpine').inside {
                        dir('backend') {
                            sh 'chmod +x gradlew'
                            sh './gradlew clean build --stacktrace -x test'
                        }
                    }
                }
            }
        }

        stage('Deploy with Docker Compose for Test') {
            steps {
                script {
                    echo 'Deploying with Docker Compose for testing...'
                    dir('backend') {
                        sh 'docker-compose up --build -d'
                    }
                }
            }
        }

        stage('yml file copy for Prod') {
            steps {
                script {
                    dir('backend') {
                        withCredentials([file(credentialsId: 'prod-application-yml', variable: 'PROD_APPLICATION_YML')]) {
                            sh '''
                            cp -f $PROD_APPLICATION_YML ./src/main/resources/application.yml
                            '''
                        }
                    }
                }
            }
        }

        stage('Build Spring Boot App for Prod') {
            steps {
                script {
                    echo 'Building Spring Boot app for production...'
                    docker.image('eclipse-temurin:17-jdk-alpine').inside {
                        dir('backend') {
                            sh 'chmod +x gradlew'
                            sh './gradlew clean build --stacktrace -x test'
                        }
                    }
                }
            }
        }

        stage('Build Docker Image for Prod') {
            steps {
                script {
                    echo "Building Docker image with tag: ${GIT_COMMIT}..."

                    dir('backend') {
                        sh """
                        docker build -t 575108921650.dkr.ecr.ap-northeast-2.amazonaws.com/mysafedraw/backend:${GIT_COMMIT} .
                        """
                    }
                }
            }
        }

        stage('AWS CLI Login') {
            steps {
                withAWS(credentials: 'aws-credentials-id', region: 'ap-northeast-2') {
                    sh '''
                    aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 575108921650.dkr.ecr.ap-northeast-2.amazonaws.com
                    '''
                }
            }
        }

        stage('Push Docker Image to AWS ECR') {
            steps {
                script {
                    echo "Pushing Docker image with tag: ${GIT_COMMIT} to AWS ECR..."
                    // 이미지 푸시 (커밋 태그와 latest 태그)
                    dir('backend') {
                        sh """
                        docker push 575108921650.dkr.ecr.ap-northeast-2.amazonaws.com/mysafedraw/backend:${GIT_COMMIT}
                        docker tag 575108921650.dkr.ecr.ap-northeast-2.amazonaws.com/mysafedraw/backend:${GIT_COMMIT} \
                                  575108921650.dkr.ecr.ap-northeast-2.amazonaws.com/mysafedraw/backend:latest
                        docker push 575108921650.dkr.ecr.ap-northeast-2.amazonaws.com/mysafedraw/backend:latest
                        """
                    }
                }
            }
        }

        stage('Cleaning up images') {
            steps {
                script {
                    echo 'Cleaning up images...'
                    sh 'docker image prune -f'
                }
            }
        }
    }
}