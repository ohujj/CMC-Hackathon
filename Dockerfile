FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN find /app/build/libs -name "*.jar" ! -name "*plain*" -exec cp {} /app/app.jar \;
ENTRYPOINT ["java", "-jar", "/app.jar"]
