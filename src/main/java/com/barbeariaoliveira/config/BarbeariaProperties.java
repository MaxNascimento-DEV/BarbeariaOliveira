package com.barbeariaoliveira.config;
import java.time.Duration;
import java.time.ZoneId;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "barbearia")
public record BarbeariaProperties(

    ZoneId fusoHorario,
    Duration antecedenciaMinima,
    int diasMaximosAntecedencia,
    Duration prazoConfirmacao,
    Duration intervaloHorarios

) {}
