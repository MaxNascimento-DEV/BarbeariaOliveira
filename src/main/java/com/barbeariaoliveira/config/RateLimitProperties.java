package com.barbeariaoliveira.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(
    Limite agendamento,
    Limite login,
    // Atrás de um proxy (Railway, Render...), getRemoteAddr() é o IP do PROXY:
    // sem isso, todos os clientes dividiriam o mesmo limite.
    boolean confiarProxy
) {

    public record Limite(
        long capacidade,
        Duration periodo
    ) {}
}
