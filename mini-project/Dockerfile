
FROM openjdk:23-jdk-oracle AS builder

WORKDIR /compileddir

COPY mvnw .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

RUN chmod a+x ./mvnw && ./mvnw package -Dmaven.test.skip=true

ENV PORT=8080
EXPOSE ${PORT}

FROM openjdk:23-jdk-oracle

ARG WORK_DIR=/app
WORKDIR ${WORK_DIR}


COPY --from=builder /compileddir/target/mini-project-0.0.1-SNAPSHOT.jar quickreports.jar


ENV PORT=8080
EXPOSE ${PORT}

ENV SPRING_DATA_REDIS_HOST=
ENV SPRING_DATA_REDIS_PORT=
ENV SPRING_DATA_REDIS_USERNAME=
ENV SPRING_DATA_REDIS_PASSWORD=
ENV SERVER_ERROR_PATH=
ENV POLYGON_API_KEY=
ENV SEC_API_KEY=


ENTRYPOINT SERVER_PORT=${PORT} java -jar quickreports.jar

