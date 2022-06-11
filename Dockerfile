FROM openjdk:11-jre-slim
VOLUME /tmp
COPY "./build/libs/*[^plain].jar" app.jar
COPY ./entrypoint.sh entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/bin/sh","entrypoint.sh"]
