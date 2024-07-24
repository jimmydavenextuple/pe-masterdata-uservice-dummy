##!/bin/bash
#OPTS="-server -XX:+UseStringDeduplication -Xlog:gc*,gc+phases=debug:file=gc.log:time,pid,tags:filecount=10,filesize=20m ${CUSTOM_JAVA_OPTS}"
#java -jar $OPTS app.jar

#!/bin/bash
if [[ -z "${USE_NEW_JAVA_OPTS}" ]]
then
    OPTS="-server -XX:+UseStringDeduplication -Xlog:gc*,gc+phases=debug:file=gc.log:time,pid,tags:filecount=10,filesize=20m ${CUSTOM_JAVA_OPTS}"
else
    OPTS="-server -Xlog:gc*,gc+phases=debug:file=gc.log:time,pid,tags:filecount=10,filesize=20m ${CUSTOM_JAVA_OPTS}"
fi

export PROFILE="$SPRING_PROFILES_ACTIVE"

if [ $PROFILE = "performance" ]; then
  java -javaagent:newrelic.jar -jar $OPTS app.jar
else
  java -jar $OPTS app.jar
fi