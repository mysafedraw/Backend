# JDK 17 버전으로 변경
FROM eclipse-temurin:17-jdk-alpine

# 임시 파일 저장 위치 설정
VOLUME /tmp

# JAR 파일을 backend.jar로 복사
COPY ./build/libs/backend-0.0.1-SNAPSHOT.jar backend.jar

# Spring Boot 애플리케이션 시작
ENTRYPOINT ["java", "-jar", "/backend.jar"]