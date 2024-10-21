# Use a base image for Java 17
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/identity-service-0.0.1-SNAPSHOT.jar /app/identity-service.jar

# Expose the port on which the identity service will run
EXPOSE 8083

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "identity-service.jar"]
