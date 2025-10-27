FROM eclipse-temurin:24-jdk AS build
WORKDIR /work

ARG TOMCAT_VERSION=10.1.48
ENV TOMCAT_DIST=/opt/apache-tomcat-${TOMCAT_VERSION}

ADD https://dlcdn.apache.org/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz /tmp/tomcat.tgz

RUN set -eux; \
    mkdir -p /opt; \
    tar -xzf /tmp/tomcat.tgz -C /opt; \
    rm -f /tmp/tomcat.tgz; \
    ls -1 "${TOMCAT_DIST}/lib" | sort | sed -n '1,120p'

COPY src src
COPY web web


RUN set -eux; \
    mkdir -p build/WEB-INF/classes; \
    if find src -type f -name '*.java' | grep -q .; then \
      javac -cp "${TOMCAT_DIST}/lib/servlet-api.jar" \
            -d build/WEB-INF/classes $(find src -type f -name '*.java'); \
    fi; \
    test -f web/WEB-INF/web.xml; \
    cp -r web/* build/; \
    jar --create --file /work/app.war -C build WEB-INF

FROM eclipse-temurin:24-jdk AS jre

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

COPY --from=jre /opt/jre-min /opt/jre-min

COPY --from=build /opt/apache-tomcat-${TOMCAT_VERSION}/bin/catalina.sh     ${CATALINA_HOME}/bin/
COPY --from=build /opt/apache-tomcat-${TOMCAT_VERSION}/bin/setclasspath.sh ${CATALINA_HOME}/bin/
COPY --from=build /opt/apache-tomcat-${TOMCAT_VERSION}/bin/version.sh      ${CATALINA_HOME}/bin/
COPY --from=build /opt/apache-tomcat-${TOMCAT_VERSION}/bin/bootstrap.jar   ${CATALINA_HOME}/bin/
COPY --from=build /opt/apache-tomcat-${TOMCAT_VERSION}/bin/tomcat-juli.jar ${CATALINA_HOME}/bin/

COPY --from=build /opt/apache-tomcat-${TOMCAT_VERSION}/conf/ ${CATALINA_HOME}/conf/

COPY --from=build /opt/apache-tomcat-${TOMCAT_VERSION}/lib/ ${CATALINA_HOME}/lib/
RUN set -eux; cd ${CATALINA_HOME}/lib; \
    rm -f jasper*.jar ecj-*.jar || true; \
    rm -f tomcat-i18n-*.jar || true; \
    rm -f tomcat-websocket*.jar websocket*.jar || true; \
    rm -f catalina-tribes.jar catalina-storeconfig.jar || true; \
    ls -1 | sort | sed -n '1,200p'

COPY --from=build /work/app.war ${CATALINA_HOME}/webapps/ROOT.war

RUN set -eux; \
    chmod +x ${CATALINA_HOME}/bin/*.sh; \
    useradd -r -d ${CATALINA_HOME} -s /sbin/nologin tomcat; \
    chown -R tomcat:tomcat ${CATALINA_HOME}
USER tomcat

ENV CATALINA_OPTS="-Xms16m -XX:MaxRAMPercentage=40 -XX:+UseSerialGC -Dfile.encoding=UTF-8"

EXPOSE 8080
CMD ["/opt/tomcat/bin/catalina.sh","run"]
