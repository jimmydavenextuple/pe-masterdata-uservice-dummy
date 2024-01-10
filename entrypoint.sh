#!/bin/bash
OPTS="-server -XX:+UseStringDeduplication -Xlog:gc*,gc+phases=debug:file=gc.log:time,pid,tags:filecount=10,filesize=20m ${CUSTOM_JAVA_OPTS}"
java -javaagent:/agent.jar=address=localhost,port=6300,output=tcpclient -jar  $OPTS app.jar