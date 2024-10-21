# Use a base image for Java 17
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/book-service-0.0.1-SNAPSHOT.jar /app/book-service.jar

# Expose the port on which the service will run
EXPOSE 8082

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "book-service.jar"]
