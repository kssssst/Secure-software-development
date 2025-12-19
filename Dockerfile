# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven files first (for better caching)
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Create non-root user
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create SSL directory (keystore will be generated at runtime or copied)
RUN mkdir -p /app/ssl

# Expose ports
EXPOSE 8443  # HTTPS
EXPOSE 8080  # HTTP (for redirect)

# Environment variable for keystore password
ENV KEYSTORE_PASSWORD=${KEYSTORE_PASSWORD}

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -k -f https://localhost:8443/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
