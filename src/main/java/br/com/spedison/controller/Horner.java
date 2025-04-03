package br.com.spedison.controller;

import br.com.spedison.Main;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Horner extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(Horner.class);


    private double classico(double[] parametros, double x) {
        double ret = 0.;
        for (int i = 0; i < parametros.length; i++) {
            ret += Math.pow(x, i) * parametros[i];
        }
        return ret;
    }

    private double horner(double[] parametros, double x) {
        double ret = 0.;
        int i = parametros.length - 1;
        while (i >= 0) {
            ret = parametros[i] + x * ret;
            i--;
        }
        return ret;
    }

    void comparaExecucoes(double[] constantes, PrintWriter p) {
        double x = 1.0;
        double tempo1 = System.nanoTime();
        for (x = -10.; x <= 10.; x += .2) {
            classico(constantes, x);
        }
        double tempo2 = System.nanoTime();
        p.println("Tempo gasto pelo método clássico: " + (tempo2 - tempo1) + " ns");

        tempo1 = System.nanoTime();
        for (x = -10.; x <= 10.; x += .2) {
            horner(constantes, x);
        }
        tempo2 = System.nanoTime();
        p.println("Tempo gasto pelo método Horner: " + (tempo2 - tempo1) + " ns");

        int contaDiferencas = 0;
        int contaTotal = 0;
        for (x = -10.; x <= 10.; x += .2) {
            contaTotal++;
            double c = classico(constantes, x);
            double h = horner(constantes, x);
            if (Math.abs(c - h) > 1.e-6) {
                p.println("Erro na função horner para x=" + x + " : " + c + "!= " + h);
                contaDiferencas++;
            }
        }
        p.println("Total de erros: " + contaDiferencas + " de " + contaTotal);
    }

    double[] leParametros(String parametros) {
        // Primeiro remove HTML encoding: &quot; -> "
        //String htmlDecoded = StringEscapeUtils.unescapeHtml4(input);
        // Depois remove URL encoding: %2C -> ,
        String urlDecoded = URLDecoder.decode(parametros, StandardCharsets.UTF_8);

        String[] parte = urlDecoded.split("&");
        for (String pa : parte) {
            String[] p = pa.split("=");
            if (p[0].equalsIgnoreCase("constantes")) {
                String[] c = p[1].split(",");
                double[] ret = new double[c.length];
                for (int i = 0; i < c.length; i++) {
                    ret[i] = Double.parseDouble(c[i]);
                }
                return ret;
            }
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String a = req.getQueryString();
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter p = resp.getWriter();
        double[] constantes = leParametros(a);
        comparaExecucoes(constantes, p);
        p.println("Linha de comando :: " + a);
        p.flush();
    }
}
