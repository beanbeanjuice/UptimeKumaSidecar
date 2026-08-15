FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencies

COPY src ./src
RUN ./gradlew --no-daemon shadowJar

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S sidecar && adduser -S sidecar -G sidecar
COPY --from=build /app/build/libs/*.jar app.jar
USER sidecar

ENTRYPOINT ["java", "-jar", "app.jar"]
