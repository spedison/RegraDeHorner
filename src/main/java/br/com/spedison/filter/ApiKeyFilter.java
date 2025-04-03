package br.com.spedison.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.LogRecord;

public class ApiKeyFilter implements Filter {
    private String expectedKey;


    @Override
    public void init(FilterConfig filterConfig) {
        this.expectedKey = filterConfig.getInitParameter("expectedKey");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String key = httpReq.getHeader("X-API-Key");

        if (expectedKey.equals(key)) {
            chain.doFilter(request, response);
        } else {
            httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResp.getWriter().write("Acesso inválido");
        }
    }

    @Override
    public void destroy() {}
}
