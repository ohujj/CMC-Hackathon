# 1. JDK 21 기반 이미지
FROM openjdk:21-jdk-slim

# 2. JAR 파일 복사
COPY build/libs/*.jar app.jar

# 3. 실행
ENTRYPOINT ["java","-jar","/app.jar"]