package com.barbeariaoliveira.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Origens (front-ends) que podem chamar a API pelo navegador. Ex.: http://localhost:5173
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(List<String> origensPermitidas) {
}
