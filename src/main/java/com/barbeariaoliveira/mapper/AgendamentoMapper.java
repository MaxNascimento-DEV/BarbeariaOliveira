package com.barbeariaoliveira.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;
import com.barbeariaoliveira.config.BarbeariaProperties;
import com.barbeariaoliveira.dto.AgendamentoResponse;
import com.barbeariaoliveira.entity.Agendamento;
import com.barbeariaoliveira.notificacao.WhatsAppLinkBuilder;

@Component
public class AgendamentoMapper {

    private final BarbeariaProperties barbeariaProperties;
    private final WhatsAppLinkBuilder whatsAppLinkBuilder;

    public AgendamentoMapper(BarbeariaProperties barbeariaProperties,
                             WhatsAppLinkBuilder whatsAppLinkBuilder) {
        this.barbeariaProperties = barbeariaProperties;
        this.whatsAppLinkBuilder = whatsAppLinkBuilder;
    }

    public AgendamentoResponse toResponse(Agendamento agendamento){
        ZoneId fuso = barbeariaProperties.fusoHorario();
        LocalDateTime inicioLocal = LocalDateTime.ofInstant(agendamento.getDataHoraInicio(), fuso);

        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getServico().getNome(),
                agendamento.getBarbeiro().getNome(),
                agendamento.getNomeCliente(),
                inicioLocal,
                LocalDateTime.ofInstant(agendamento.getDataHoraFim(), fuso),
                agendamento.getValorCobrado(),
                agendamento.getStatus(),
                whatsAppLinkBuilder.linkParaBarbearia(agendamento, inicioLocal),
                whatsAppLinkBuilder.linkParaCliente(agendamento, inicioLocal)
        );
    }
}
