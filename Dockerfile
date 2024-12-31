# Stage 1: Build the application
FROM openjdk:17 AS build
WORKDIR /app

# Copy Maven wrapper and configuration
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Ensure the Maven wrapper script is executable
RUN chmod +x mvnw

# Download dependencies (this step will be cached unless pom.xml changes)
RUN ./mvnw dependency:resolve

# Copy the source code
COPY src ./src

# Build the application
RUN ./mvnw clean install -DskipTests

# Stage 2: Create a lightweight runtime image
FROM openjdk:17-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar /app/sensor-data-producer.jar

# Expose the application port
EXPOSE 8080

# Define the command to run your application with the 'docker' profile
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-jar", "/app/sensor-data-producer.jar"]
