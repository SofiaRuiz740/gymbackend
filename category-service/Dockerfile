FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY category-service/target/category-service-1.0.0.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
