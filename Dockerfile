# Dockerfile
FROM maven:3.6.3-openjdk-11

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests
