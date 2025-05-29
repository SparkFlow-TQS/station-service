FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy dependency files first
COPY pom.xml mvnw ./
COPY .mvn ./.mvn

# Install dependencies
RUN ./mvnw dependency:go-offline

# Copy the rest of the source code
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"] 