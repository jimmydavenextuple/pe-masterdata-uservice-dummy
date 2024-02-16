FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY "./build/libs/*[^plain].jar" app.jar
EXPOSE 8080
COPY ./deployment-scripts/jacoco/jacocoagent.jar agent.jar
COPY ./deployment-scripts/jacoco/jacocoserver.jar server.jar
COPY ./entrypoint.sh entrypoint.sh
ENTRYPOINT ["/bin/sh","entrypoint.sh"]