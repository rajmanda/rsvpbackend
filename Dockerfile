# Use the official Maven image to build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Package the application
RUN mvn package -DskipTests

# Use Eclipse Temurin for the runtime stage
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory in the second stage
WORKDIR /app

# Install curl and other common tools (e.g., bash, wget)
RUN rm -rf /var/lib/apt/lists/* && \
    apt-get update && \
    apt-get install -y --no-install-recommends curl wget bash && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy the packaged JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port (adjust if necessary)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
