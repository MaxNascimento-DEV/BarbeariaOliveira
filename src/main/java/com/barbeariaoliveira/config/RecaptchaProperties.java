package com.barbeariaoliveira.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "recaptcha")
public record RecaptchaProperties(
    boolean habilitado,
    String segredo,
    double notaMinima,
    String url
) {}
