FROM gradle:jdk25 AS build

WORKDIR /app
COPY . ./

RUN chmod +x ./gradlew
RUN gradle shadowJar

FROM eclipse-temurin:25-jre AS runtime

ARG UID=1001
ARG GID=1001
RUN groupadd -g ${GID} appuser \
    && useradd -l -u ${UID} -g ${GID} -m appuser

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

USER sidecar

ENV JAVA_OPTS="-Xmx3G"

ENTRYPOINT ["java", "-jar", "app.jar"]
