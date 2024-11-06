pipeline {
    agent any

    environment {
        CI = 'false'
    }

    stages {
        stage('Build Spring Boot App') {
            steps {
                script {
                    echo 'Building Spring Boot app...'
                    
                    // Docker 컨테이너를 사용하여 빌드 실행 
                    docker.image('eclipse-temurin:17-jdk-alpine').inside {
                        dir('backend') {
                            sh 'chmod +x gradlew'
                            sh './gradlew clean build --stacktrace -x test'
                        }
                    }
                }
            }
        }

        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo 'Deploying with Docker Compose...'
                    
                    // Docker Compose 실행
                    dir('backend') {
                        sh 'docker-compose up --build -d'
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
