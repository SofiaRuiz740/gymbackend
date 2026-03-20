FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY report-service/target/report-service-1.0.0.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
