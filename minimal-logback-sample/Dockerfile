FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /work

ARG TOMCAT_VERSION=10.1.48
ENV TOMCAT_DIST=/opt/tomcat

RUN curl -fsSL --retry 3 -o /tmp/tomcat.tgz \
    https://dlcdn.apache.org/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz

RUN set -eux; \
    mkdir -p /opt/tomcat; \
    tar -xzf /tmp/tomcat.tgz --strip-components=1 -C /opt/tomcat; \
    rm -f /tmp/tomcat.tgz; \
    ls -1 "${TOMCAT_DIST}/lib" | sort | sed -n '1,120p'

COPY src src
COPY web web
COPY pom.xml /work

RUN mvn package

RUN set -eux; \
    mkdir -p build/WEB-INF/classes; \
    if find src -type f -name '*.java' | grep -q .; then \
      javac -cp "/work/lib/jakarta.servlet-api-6.1.0.jar:/work/lib/slf4j-api-2.0.17.jar" \
            -d build/WEB-INF/classes $(find src -type f -name '*.java'); \
    fi; \
    test -f web/WEB-INF/web.xml; \
    cp -r web/* build/; \
    jar --create --file /work/app.war -C build WEB-INF

RUN jlink \
  --add-modules \
  java.base,java.logging,java.xml,java.management,java.naming,java.rmi,java.sql,java.desktop,java.security.jgss,jdk.unsupported,jdk.charsets,java.instrument \
  --strip-debug --no-man-pages --no-header-files --compress=2 \
  --output /opt/jre-min

FROM debian:stable-slim

ARG TOMCAT_VERSION=10.1.48
ENV CATALINA_HOME=/opt/tomcat \
    PATH=/opt/jre-min/bin:$PATH

RUN set -eux; \
    mkdir -p ${CATALINA_HOME}/bin ${CATALINA_HOME}/lib ${CATALINA_HOME}/conf \
             ${CATALINA_HOME}/webapps ${CATALINA_HOME}/logs \
             ${CATALINA_HOME}/temp ${CATALINA_HOME}/work

COPY --from=build /opt/jre-min /opt/jre-min

COPY --from=build ${CATALINA_HOME}/bin/catalina.sh     ${CATALINA_HOME}/bin/
COPY --from=build ${CATALINA_HOME}/bin/setclasspath.sh ${CATALINA_HOME}/bin/
COPY --from=build ${CATALINA_HOME}/bin/version.sh      ${CATALINA_HOME}/bin/
COPY --from=build ${CATALINA_HOME}/bin/bootstrap.jar   ${CATALINA_HOME}/bin/
COPY --from=build ${CATALINA_HOME}/bin/tomcat-juli.jar ${CATALINA_HOME}/bin/

COPY bin/setenv.sh ${CATALINA_HOME}/bin/

COPY --from=build ${CATALINA_HOME}/conf/ ${CATALINA_HOME}/conf/

COPY conf/. ${CATALINA_HOME}/conf/

COPY --from=build ${CATALINA_HOME}/lib/ ${CATALINA_HOME}/lib/

COPY --from=build /work/lib/ ${CATALINA_HOME}/lib/

RUN set -eux; cd ${CATALINA_HOME}/lib; \
    rm -f jasper*.jar ecj-*.jar || true; \
    rm -f tomcat-i18n-*.jar || true; \
    rm -f tomcat-websocket*.jar websocket*.jar || true; \
    rm -f catalina-tribes.jar catalina-storeconfig.jar || true; \
    ls -1 | sort | sed -n '1,200p'

COPY --from=build /work/app.war ${CATALINA_HOME}/webapps/ROOT.war


EXPOSE 8080
CMD ["/opt/tomcat/bin/catalina.sh","run"]
