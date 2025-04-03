package br.com.spedison;

import br.com.spedison.utils.ServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.LogManager;


public class Main {

    static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        log.info("Iniciando o servidor...");
        ServerUtils su = new ServerUtils(8888, "0.0.0.0", "chave");
        try {
            su.inicia();
        } catch (Exception e) {
            log.error("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}