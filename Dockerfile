#FROM eclipse-temurin:21-jdk-jammy
FROM eclipse-temurin:21-jre

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]