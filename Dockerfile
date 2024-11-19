FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY /tmp/artifacts/libs/*.jar app.jar
EXPOSE 8080
COPY ./entrypoint.sh entrypoint.sh
ENTRYPOINT ["/bin/sh","entrypoint.sh"]