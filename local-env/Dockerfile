FROM openjdk:13
VOLUME /tmp
ARG PROFILE
COPY ./rest-api-ms.jar app.jar
ENTRYPOINT java -jar app.jar --port ${APP_PORT} --kafka ${KAFKA_URL} --redisHost ${REDIS_HOST} --redisPort ${REDIS_PORT} --redisPassword ${REDIS_PASSWORD}