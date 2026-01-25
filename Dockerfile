# -------- Build Stage --------
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# -------- Runtime Stage --------
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

# # -------- Build Stage --------
# FROM maven:3.9.9-eclipse-temurin-17 AS build
# WORKDIR /app
# COPY pom.xml .
# RUN mvn -B -q dependency:go-offline
#
# COPY src ./src
# RUN mvn -B package -DskipTests
#
# # -------- Runtime Stage --------
# FROM eclipse-temurin:17-jre
# WORKDIR /app
#
# COPY --from=build /app/target/*.jar app.jar
#
# # Fly uses 8080 by default
# EXPOSE 8080
#
# ENV JAVA_OPTS="-Xms128m -Xmx192m -XX:+UseSerialGC -Djava.security.egd=file:/dev/./urandom"
#
# ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
