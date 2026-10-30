package com.barbeariaoliveira.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limit")
public record RateLimitProperties(
    Limite agendamento,
    Limite login
) {

    public record Limite(
        long capacidade,
        Duration periodo
    ) {}
}
