#!/bin/sh

classpath=${CATALINA_HOME}/bin/bootstrap.jar:${CATALINA_HOME}/bin/tomcat-juli.jar

for jar in ${CATALINA_HOME}/lib/*

do
  classpath=${classpath}:$jar
done

export CLASSPATH=${classpath}

CATALINA_OPTS="$CATALINA_OPTS -Djava.security.egd=file:/dev/urandom"
CATALINA_OPTS="$CATALINA_OPTS -Djava.util.logging.config.file=$CATALINA_BASE/conf/logging.properties"
CATALINA_OPTS="$CATALINA_OPTS -Dlogback.configurationFile=$CATALINA_BASE/conf/logback.xml"

CATALINA_OPTS="$CATALINA_OPTS --enable-native-access=ALL-UNNAMED"
#CATALINA_OPTS="$CATALINA_OPTS -XshowSettings:vm -XshowSettings:properties"
CATALINA_OPTS="$CATALINA_OPTS -Xms16m -XX:MaxRAMPercentage=40 -XX:+UseSerialGC -Dfile.encoding=UTF-8"


export CATALINA_OPTS
