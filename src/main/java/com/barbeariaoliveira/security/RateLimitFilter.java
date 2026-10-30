package com.barbeariaoliveira.security;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.barbeariaoliveira.config.RateLimitProperties;
import com.barbeariaoliveira.config.RateLimitProperties.Limite;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RateLimitFilter extends OncePerRequestFilter {

    private static final String ROTA_AGENDAMENTO = "/api/public/agendamentos";
    private static final String ROTA_LOGIN = "/api/public/auth/login";

    private final RateLimitProperties properties;
    private final Map<String, Bucket> baldes = new ConcurrentHashMap<>();

    public RateLimitFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Limite limite = escolherLimite(request);

        if (limite == null) {
            filterChain.doFilter(request, response);   // rota sem limite: segue
            return;
        }

        String chave = request.getRequestURI() + "|" + request.getRemoteAddr();
        Bucket balde = baldes.computeIfAbsent(chave, k -> criarBalde(limite));

        if (balde.tryConsume(1)) {
            filterChain.doFilter(request, response);   // tinha ficha: segue
        } else {
            responderMuitasRequisicoes(response);      // balde vazio: para aqui
        }
    }

    private Limite escolherLimite(HttpServletRequest request) {
        if (!"POST".equals(request.getMethod())) {
            return null;
        }
        return switch (request.getRequestURI()) {
            case ROTA_AGENDAMENTO -> properties.agendamento();
            case ROTA_LOGIN -> properties.login();
            default -> null;
        };
    }

    private Bucket criarBalde(Limite limite) {
        return Bucket.builder()
                .addLimit(l -> l.capacity(limite.capacidade())
                                .refillGreedy(limite.capacidade(), limite.periodo()))
                .build();
    }

    private void responderMuitasRequisicoes(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("""
                {"status":429,"mensagem":"Muitas tentativas. Aguarde alguns minutos e tente novamente.","timestamp":"%s","campos":null}
                """.formatted(Instant.now()));
    }
}
