package com.barbeariaoliveira.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.barbeariaoliveira.enums.StatusAgendamento;

public record AgendamentoResponse(
    Long id,
    String nomeServico,
    String nomeBarbeiro,
    String nomeCliente,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraFim,
    BigDecimal valorCobrado,
    StatusAgendamento status
) {}