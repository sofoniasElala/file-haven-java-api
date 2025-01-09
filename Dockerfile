FROM eclipse-temurin:17-jdk-focal
 
# Set the working directory in the container
WORKDIR /app

# Copy the pre-built JAR file into the container
COPY target/file-haven-java-api-0.0.1-SNAPSHOT.jar /app/my-app.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "my-app.jar"]