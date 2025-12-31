# --- Build Stage ---
FROM gradle:8.5-jdk21 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle :app:shadowJar --no-daemon

# --- Run Stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the Fat JAR from build stage
COPY --from=build /home/gradle/src/app/build/libs/app-all.jar /app/app.jar

# Copy the trained model (assuming it's in the root context)
# You must have mnist_model.json in your project root locally!
COPY mnist_model.json /app/mnist_model.json

# Expose port (Cloud Run sets PORT env var, but 8080 is common default)
EXPOSE 8080

# Run the server mode (Mode 2)
# We pass "2" as argument to skip the menu
CMD ["java", "-jar", "/app/app.jar", "2"]
