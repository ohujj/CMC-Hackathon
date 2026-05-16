FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon -x test && \
    find build/libs -name "*.jar" ! -name "*plain*" -exec cp {} app.jar \;

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
