FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/InitiateService.jar /app/InitiateService.jar
EXPOSE 8093
CMD ["java", "-jar", "/app/InitiateService.jar"]