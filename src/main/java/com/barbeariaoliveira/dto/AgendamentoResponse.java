package com.barbeariaoliveira.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.barbeariaoliveira.enums.StatusAgendamento;

public record AgendamentoResponse(
    Long id,
    Long servicoId,
    String nomeServico,
    Long barbeiroId,
    String nomeBarbeiro,
    String nomeCliente,
    String telefoneCliente,
    LocalDateTime dataHoraInicio,
    LocalDateTime dataHoraFim,
    BigDecimal valorCobrado,
    StatusAgendamento status,
    String linkWhatsAppBarbearia,   // botão do cliente: "Enviar meu agendamento para a barbearia"
    String linkWhatsAppCliente      // botão do painel admin: "Avisar cliente"
) {}
