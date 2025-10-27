# Minimal Logback + Spring (WAR for Tomcat 10/11)

## Build
```bash
mvn -q -DskipTests clean package
```
This produces `target/minimal-logback-spring.war`.

## Deploy
Copy the WAR to `$CATALINA_BASE/webapps/`. Tomcat will unpack it to `/minimal-logback-spring`.

Assumes you already place logging JARs and `logback.xml` in `$CATALINA_BASE/lib` and `$CATALINA_BASE/conf` respectively (as in your minimal logback setup).

## Endpoints
- `GET /minimal-logback-spring/simple?name=Denis` — plain Servlet that uses a Spring-managed bean.
- `GET /minimal-logback-spring/api/hello?name=Denis` — Spring MVC controller.

## Notes
- Uses Spring 6 (Jakarta namespace) and Servlet 6 to match Tomcat 10+.
- Logging libraries are marked as `provided` so they can be supplied by Tomcat per your setup. If you prefer them inside the WAR, remove `<scope>provided</scope>`.
