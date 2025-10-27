package com.sample.minimal.jackson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "MinimalJacksonServlet")
public class MinimalJacksonServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MinimalJacksonServlet.class);

    private ObjectMapper json;
    private XmlMapper xml;

    @Override
    public void init() throws ServletException {
        this.json = new ObjectMapper();
        this.xml  = new XmlMapper();
        log.info("MinimalJacksonServlet iniciado");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Roteamento mínimo: /api/messages/{id}
        String path = req.getPathInfo(); // ex: /messages/123
        if (path == null || !path.startsWith("/messages/")) {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Not Found");
            return;
        }
        String id = path.substring("/messages/".length());
        if (id.isEmpty()) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing id");
            return;
        }
        String name = req.getParameter("name");
        if (name == null || name.isBlank()) name = "world";
        Message payload = new Message(id, "Hello, " + name + "!");

        // Negociação: ?format=json|xml|html|txt tem prioridade; senão usa Accept
        String format = req.getParameter("format");
        if (format != null) format = format.toLowerCase(Locale.ROOT);

        MediaType mt = (format != null)
                ? MediaType.fromFormat(format)
                : MediaType.fromAcceptHeader(req.getHeaders("Accept"));

        switch (mt) {
            case JSON -> writeJson(resp, payload);
            case XML  -> writeXml(resp, payload);
            case HTML -> writeHtml(resp, payload);
            case TEXT -> writeText(resp, payload);
            default   -> writeJson(resp, payload); // default
        }
    }

    private void writeJson(HttpServletResponse resp, Object body) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/json;charset=UTF-8");
        json.writeValue(resp.getOutputStream(), body);
        log.debug("Resposta JSON enviada");
    }

    private void writeXml(HttpServletResponse resp, Object body) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("application/xml;charset=UTF-8");
        xml.writeValue(resp.getOutputStream(), body);
        log.debug("Resposta XML enviada");
    }

    private void writeText(HttpServletResponse resp, Message m) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().printf("id=%s, content=%s%n", m.getId(), m.getContent());
        log.debug("Resposta TEXT enviada");
    }

    private void writeHtml(HttpServletResponse resp, Message m) throws IOException {
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setContentType("text/html;charset=UTF-8");
        String safeContent = escape(m.getContent());
        String safeId = escape(m.getId());
        resp.getWriter().write("""
      <!doctype html><html><head><meta charset="utf-8"><title>Message</title></head>
      <body><h1>%s</h1><p>id=%s</p></body></html>
      """.formatted(safeContent, safeId));
        log.debug("Resposta HTML enviada");
    }

    private static String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }

    private void sendError(HttpServletResponse resp, int status, String msg) throws IOException {
        log.warn("Erro {}: {}", status, msg);
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        json.writeValue(resp.getOutputStream(), new Message(String.valueOf(status), msg));
    }

    enum MediaType { JSON, XML, HTML, TEXT;
        static MediaType fromFormat(String f) {
            return switch (f) {
                case "json" -> JSON; case "xml" -> XML; case "html" -> HTML; case "txt", "text" -> TEXT;
                default -> JSON;
            };
        }
        static MediaType fromAcceptHeader(java.util.Enumeration<String> accepts) {
            // Ordem simples por preferência comum
            List<String> prefs = List.of("application/json", "application/xml", "text/html", "text/plain");
            while (accepts != null && accepts.hasMoreElements()) {
                String header = accepts.nextElement().toLowerCase(Locale.ROOT);
                for (String p : prefs) if (header.contains(p)) {
                    return switch (p) {
                        case "application/json" -> JSON;
                        case "application/xml"  -> XML;
                        case "text/html"        -> HTML;
                        default                 -> TEXT;
                    };
                }
            }
            return JSON;
        }
    }
}