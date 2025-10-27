# Java + Tomcat Samples

Este repositório contém **exemplos incrementais** de aplicações Java usando **Servlets** no **Tomcat**, com foco em tamanhos reduzidos de imagem Docker, JRE mínima e camadas progressivas de abstração.

Cada subpasta representa um **sample** independente, com seu próprio `Dockerfile`, `docker-compose.yml` e `README.md`.

## Estrutura

```
.
├─ minimal-sample/         # Primeiro sample: Servlet básico + Tomcat destilado
├─ (futuros samples)...    # Próximos exemplos incrementais
└─ README.md               # Este arquivo
```

## Samples atuais s

### [minimal-sample](minimal-sample/)
- **Objetivo:** menor aplicação possível em Java + Tomcat
- **Tecnologias:** Servlet + web.xml, Tomcat 10.1.46 destilado, JRE mínima via jlink
- **Destaques:** imagem Docker pequena, startup rápido, sem JSP nem frameworks adicionais

## Como usar

Cada sample tem seu próprio README explicando:
- Estrutura da pasta
- Comando para buildar e subir com `docker compose`
- Endpoints disponíveis
- Observações sobre footprint e módulos usados no jlink

Basta entrar na pasta desejada, por exemplo:

```bash
cd minimal-sample
docker compose up --build
```

Acesse no navegador:

```
http://localhost:8080/
```

*(O contexto path pode variar conforme cada sample.)*

## Roadmap de Samples

A ideia é evoluir gradualmente:
1. **minimal-sample:** Servlet + Tomcat puro
2. **mini-di-sample:** mesmo base mas com um micro container DI (providers/singletons)
3. **filters-sample:** adicionando Filters e Listeners para middleware cross-cutting
4. **mvc-sample:** organizando servlets e views estilo MVC sem Spring
5. **spring-like-sample:** adicionando um mini ApplicationContext para aproximar do Spring

## Licença

- **Apache Tomcat**: Apache License 2.0  
- **Eclipse Temurin JDK**: GPLv2+CE  
- Este repositório: MIT License (a definir)
