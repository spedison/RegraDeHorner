package br.com.spedison.utils;

import br.com.spedison.controller.AppRun;
import br.com.spedison.controller.Horner;
import br.com.spedison.filter.ApiKeyFilter;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerUtils {

    static final Logger log = LoggerFactory.getLogger(ServerUtils.class);
    AtomicBoolean processamentoRodando = new AtomicBoolean(false);
    int port;
    String bindAddress;
    String chave;

    public ServerUtils(int port, String bindAddress, String chave) {
        this.port = port;
        this.bindAddress = bindAddress;
        this.chave = chave;
    }

    public void inicia() throws Exception {

        String contextPath = "/app";

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setHostname(bindAddress);
        tomcat.getConnector(); // força a criação do conector

        File base = new File(System.getProperty("java.io.tmpdir"));
        var ctx = tomcat.addContext(contextPath, base.getAbsolutePath());

        Tomcat.addServlet(ctx, "runServlet", new AppRun(processamentoRodando));
        ctx.addServletMappingDecoded("/run", "runServlet");

        Tomcat.addServlet(ctx, "runHorner", new Horner());
        ctx.addServletMappingDecoded("/horner", "runHorner");

        var filterDef = new FilterDef();
        filterDef.setFilter(new ApiKeyFilter());
        filterDef.setFilterName("apiKeyFilter");
        filterDef.addInitParameter("expectedKey", chave);
        ctx.addFilterDef(filterDef);

        var filterMap = new FilterMap();
        filterMap.setFilterName("apiKeyFilter");
        filterMap.addURLPattern("/app");
        ctx.addFilterMap(filterMap);

        // Inicializa o Tomcat
        tomcat.start();

        // Latch para bloquear a main thread até o shutdown
        CountDownLatch latch = new CountDownLatch(1);

        // Hook para encerrar com elegância
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {

            log.info("Encerrando aplicação...");

            while (processamentoRodando.get() == true) {
                try {
                    Thread.sleep(1_000);
                    log.info("Aguardando processamento terminar...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                tomcat.stop();
                tomcat.destroy();
                log.info("Tomcat encerrado com sucesso.");
            } catch (LifecycleException e) {
                log.error("Erro ao parar o Tomcat: " + e.getMessage());
            }

            latch.countDown(); // Libera o await()
        }));

        log.info("Servidor rodando em http://%s:%d%s/run%n".formatted(bindAddress, port, contextPath));

        // Aguarda o shutdown
        latch.await();

        log.info("Aplicação finalizada.");
    }
}