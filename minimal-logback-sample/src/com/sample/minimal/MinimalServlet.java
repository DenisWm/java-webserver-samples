package com.sample.minimal;

import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MinimalServlet extends HttpServlet {

    public Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        logger.info("Minimal Servlet INFO");
        logger.warn("Minimal Servlet WARN");
        resp.setContentType("text/html; charset=UTF-8");
        resp.getWriter().write("""
      <!DOCTYPE html>
      <html lang="pt-BR">
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <title>Mini Tomcat App</title>
          <style>
            body {
              font-family: Arial, sans-serif;
              background: linear-gradient(135deg, #74ebd5 0%, #9face6 100%);
              margin: 0; padding: 0;
              display: flex; align-items: center; justify-content: center;
              height: 100vh; color: #333;
            }
            .card {
              background: #fff;
              border-radius: 12px;
              box-shadow: 0 4px 15px rgba(0,0,0,0.2);
              padding: 2rem;
              text-align: center;
              max-width: 400px;
              animation: fadeIn 1s ease;
            }
            h1 {
              margin: 0 0 1rem 0;
              font-size: 1.8rem;
              color: #444;
            }
            p {
              margin: 0.5rem 0;
              font-size: 1rem;
              color: #666;
            }
            .btn {
              display: inline-block;
              margin-top: 1.5rem;
              padding: 0.6rem 1.2rem;
              background: #4a90e2;
              color: white;
              border-radius: 6px;
              text-decoration: none;
              transition: background 0.3s;
            }
            .btn:hover {
              background: #357abd;
            }
            @keyframes fadeIn {
              from { opacity: 0; transform: translateY(20px); }
              to   { opacity: 1; transform: translateY(0); }
            }
          </style>
        </head>
        <body>
          <div class="card">
            <h1>🚀 Aplicação Java Minimalista</h1>
            <p>Rodando no <strong>Tomcat 10.1.46</strong> com <strong>Java 24</strong>.</p>
            <p>WAR construído sem Maven/Gradle, apenas com <code>javac</code> e <code>jar</code>.</p>
            <a href="https://tomcat.apache.org" class="btn" target="_blank">Saiba mais</a>
          </div>
        </body>
      </html>
    """);
    }
}