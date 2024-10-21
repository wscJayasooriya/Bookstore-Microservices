# Use a base image for Java 17
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/api-gateway-service-0.0.1-SNAPSHOT.jar /app/api-gateway-service.jar

# Expose the port on which the API Gateway will run
EXPOSE 8080

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "api-gateway-service.jar"]