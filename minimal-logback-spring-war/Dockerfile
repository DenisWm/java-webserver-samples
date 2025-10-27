FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .

RUN mvn -q -B -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -q -B -DskipTests clean package \
    dependency:copy-dependencies -DincludeScope=provided -DoutputDirectory=target/provided-libs

FROM tomcat:10.1.46-jre17-temurin

ENV CATALINA_BASE=/usr/local/tomcat \
    TZ=America/Sao_Paulo \
    LANG=C.UTF-8

RUN rm -rf "$CATALINA_BASE/webapps.dist" "$CATALINA_BASE/webapps/*"
RUN rm -rf "$CATALINA_BASE/conf/logging.properties"
RUN rm -rf "$CATALINA_BASE/bin/setenv.sh"

COPY --from=build /app/target/provided-libs/*.jar $CATALINA_BASE/lib/

COPY conf/logback.xml $CATALINA_BASE/conf/logback.xml

COPY bin/setenv.sh $CATALINA_BASE/bin/

RUN echo "handlers = org.slf4j.bridge.SLF4JBridgeHandler" > ${CATALINA_BASE}/conf/logging.properties

ENV CATALINA_OPTS="$CATALINA_OPTS -Dlogback.configurationFile=$CATALINA_BASE/conf/logback.xml"

COPY --from=build /app/target/*.war $CATALINA_BASE/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]