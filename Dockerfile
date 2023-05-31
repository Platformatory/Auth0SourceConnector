# Use a base image with Java and Maven installed
FROM maven:3.8.1-openjdk-11-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file to the container
COPY pom.xml .

# Resolve Maven dependencies (this step can be cached)
RUN mvn dependency:go-offline -B

# Copy the source code to the container
COPY src/ ./src/

# Build the application
RUN mvn package -DskipTests

# Use a smaller base image for the final image
FROM openjdk:11-jre-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the previous build stage
COPY --from=build /app/target/Auth0SourceConnector-1.0-SNAPSHOT.jar .

# Set the default command to run the application
CMD ["java", "-jar", "Auth0SourceConnector-1.0-SNAPSHOT.jar"]
