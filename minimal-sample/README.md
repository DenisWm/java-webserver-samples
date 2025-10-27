# Sample Minimal — Servlet + Tomcat 10.1.46 + JRE mínima

## Visão geral
Projeto **minimal** com:

- **Servlet + web.xml** (sem Spring);
- **Tomcat 10.1.46** destilado (sem JSP, i18n, WebSocket, cluster);
- **JRE mínima** via `jlink` (apenas módulos necessários);
- **WAR** construído com `javac` e `jar` no Docker (sem Maven/Gradle).

Objetivo: **menor imagem**, **menor RAM**, startup rápido — ideal para estudos/benchmarks.

## Estrutura
```
.
├─ src/                         # .java (ex.: com/example/HelloServlet.java)
├─ web/
│  └─ WEB-INF/
│     └─ web.xml               # mapeia o servlet
├─ Dockerfile                   # multi-stage (build + jlink + runtime destilado)
└─ docker-compose.yml           # sobe o container mapeando a porta 8080
```

## Requisitos
- Docker 24+
- Docker Compose v2

*(Não requer Java ou Maven localmente.)*

## Subir com Docker Compose
```bash
docker compose up --build
```
Acesse:
- `http://localhost:8080/minimal`

## Como funciona o build
1. **Stage build**
    - Baixa **Tomcat 10.1.46**
    - Compila `src/**/*.java` com `jakarta.servlet-api.jar` do Tomcat
    - Copia `web/**` → gera `app.war`
2. **Stage jre (jlink)**
    - Cria **JRE mínima** com módulos necessários:
      `java.base,java.logging,java.xml,java.management,java.naming,java.rmi,java.sql,java.desktop,java.security.jgss,jdk.unsupported,jdk.charsets,java.instrument`
3. **Stage runtime**
    - Copia **só**: `bin/bootstrap.jar`, `bin/tomcat-juli.jar`, `conf/**` e `lib/**` essenciais (removendo `jasper*`, `ecj*`, `tomcat-i18n-*`, websocket/cluster)
    - Copia o **ROOT.war**
    - Sobe com `catalina.sh run` e JVM contida:
      ```
      -Xms16m -XX:MaxRAMPercentage=40 -XX:+UseSerialGC -Dfile.encoding=UTF-8
      ```

## Comandos úteis
```bash
# ver arquivos dentro do WAR
docker compose exec app sh -lc 'unzip -l /opt/tomcat/webapps/ROOT.war | sed -n "1,120p"'

# ver logs do Tomcat
docker compose logs -f

# checar versão Java/Tomcat dentro do container
docker compose exec app sh -lc '/opt/jre-min/bin/java -version && /opt/tomcat/bin/version.sh'
```

## Licenças
- **Apache Tomcat** (Apache License 2.0)
- **Eclipse Temurin JDK** (GPLv2+CE)
