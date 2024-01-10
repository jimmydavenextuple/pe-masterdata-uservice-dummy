FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY "./build/libs/*[^plain].jar" app.jar
EXPOSE 8080
COPY jacocoAgent.jar agent.jar
COPY jacococli.jar cli.jar
COPY ./entrypoint.sh entrypoint.sh
ENTRYPOINT ["/bin/sh","entrypoint.sh"]