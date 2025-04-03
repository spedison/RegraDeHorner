package br.com.spedison.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.logging.LogRecord;

public class ApiKeyFilter implements Filter {
    private String expectedKey;

    private static final Logger log = LoggerFactory.getLogger(ApiKeyFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) {
        this.expectedKey = filterConfig.getInitParameter("expectedKey");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        log.info("Passando pelo filtro.");

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String key = httpReq.getHeader("X-API-Key");
        log.debug("Chave recebida = " + key);

        if (expectedKey.equals(key)) {
            log.info("Chave válida. Passando para a próxima requisição.");
            chain.doFilter(request, response);
        } else {
            log.warn("Chave inválida. Rejeitando a requisição.");
            httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResp.getWriter().write("Acesso inválido");
        }
    }

    @Override
    public void destroy() {}
}
