# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -ntp dependency:go-offline

COPY src/ src/

RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -ntp clean package -DskipTests


FROM eclipse-temurin:21-jre-jammy AS runtime

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system trajectiv \
    && useradd --system --gid trajectiv --home-dir /app trajectiv \
    && mkdir -p /app/storage \
    && chown -R trajectiv:trajectiv /app

WORKDIR /app

COPY --from=build --chown=trajectiv:trajectiv \
    /workspace/target/*.jar /app/app.jar

USER trajectiv

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
