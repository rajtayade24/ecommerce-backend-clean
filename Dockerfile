FROM eclipse-temurin:24-jdk
WORKDIR /app

# Use the exact JAR name from your target folder
COPY target/instagram-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
