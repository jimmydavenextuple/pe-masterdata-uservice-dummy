FROM openjdk:11-jre-slim
VOLUME /tmp
COPY "./build/libs/*[^plain].jar" app.jar
EXPOSE 8080
ENTRYPOINT ["/bin/sh","entrypoint.sh"]