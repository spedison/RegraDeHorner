package br.com.spedison.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AppRun extends HttpServlet {
    private final AtomicBoolean rodando;
    private static Logger log = LoggerFactory.getLogger(AppRun.class);

    public AppRun(AtomicBoolean rodando) {
        this.rodando = rodando;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (rodando.get() == true) {
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().write("Está em processamento...");
            log.info("Processamento em andamento...");
        } else {
            rodando.set(true);
            resp.setContentType("text/plain;charset=UTF-8");
            resp.getWriter().write("Iniciando :: Alô mundo");
            for (int i = 10; i > 0; i--) {
                try {
                    Thread.sleep(6_000); // Simulando um processamento
                    resp.getWriter().write("Fazendo uma contagem :: %d".formatted(i));
                    log.info("Processamento em execução :: {}", i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            resp.getWriter().write("Terminado :: Alô mundo");
            log.info("Processamento concluído");
            rodando.set(false);
        }
    }
}