# Use Eclipse Temurin as the base image - it's a popular distribution of OpenJDK
FROM eclipse-temurin:21-jdk

# Set working directory in container
WORKDIR /app

# Copy your source code
COPY src /app/src

# Copy pom.xml if you're using Maven
#COPY pom.xml /app/

# If you're not using Maven, you'll need to copy any other build configuration files
# For example, for Gradle:
COPY build.gradle settings.gradle /app/
COPY gradle/ /app/gradle/
COPY gradlew /app/

# Build the application
# If using Maven:
#RUN ./mvnw clean package

# If using Gradle:
RUN ./gradlew build

# Expose the port your server runs on
EXPOSE 8080

# Run the application
# Replace 'your-app.jar' with your actual jar file name from target/
CMD ["java", "-jar", "target/your-app.jar"]