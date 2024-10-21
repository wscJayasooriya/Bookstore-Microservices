# Use a base image for Java 17
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/eureka-server-application-0.0.1-SNAPSHOT.jar /app/eureka-server-application.jar

# Expose the port on which the Eureka server will run
EXPOSE 8761

# Command to run the jar file
ENTRYPOINT ["java", "-jar", "eureka-server-application.jar"]
