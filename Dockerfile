FROM gradle:jdk25 AS build

WORKDIR /app
COPY . ./

RUN gradle shadowJar

FROM eclipse-temurin:25-jre AS runtime

ARG UID=1001
ARG GID=1001
RUN groupadd -g ${GID} sidecar \
    && useradd -l -u ${UID} -g ${GID} -m sidecar

WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

RUN chown -R sidecar:sidecar /app
USER sidecar

ENV JAVA_OPTS="-Xmx3G"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
