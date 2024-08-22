FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp
COPY "./build/libs/*[^plain].jar" app.jar
EXPOSE 8080
COPY ./newrelic/newrelic.jar newrelic.jar
COPY ./newrelic/newrelic.yml newrelic.yml
COPY ./entrypoint.sh entrypoint.sh
ENTRYPOINT ["/bin/sh","entrypoint.sh"]