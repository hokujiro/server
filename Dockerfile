# Use the official OpenJDK 17 image
FROM openjdk:17-jdk-slim

# Set a working directory
WORKDIR /app

# Copy the JAR file built by Gradle
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# Run the JAR
ENTRYPOINT ["java", "-jar", "/app/app.jar"]