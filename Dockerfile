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

# Copy MNIST dataset files
COPY app/t10k-images.idx3-ubyte /app/
COPY app/t10k-labels.idx1-ubyte /app/

# Model is not bundled in the image anymore. Provide it via env/volume.
# Set a default MODEL_PATH that can be overridden at runtime.
ENV MODEL_PATH=/app/mnist_model.json

# Expose port (Cloud Run sets PORT env var, but 8080 is common default)
EXPOSE 8080

# Run the server mode (Mode 2)
# We pass "2" as argument to skip the menu
CMD ["java", "-jar", "/app/app.jar", "2"]
