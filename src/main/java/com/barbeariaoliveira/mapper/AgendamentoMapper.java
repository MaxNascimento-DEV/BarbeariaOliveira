package com.barbeariaoliveira.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.stereotype.Component;
import com.barbeariaoliveira.config.BarbeariaProperties;
import com.barbeariaoliveira.dto.AgendamentoResponse;
import com.barbeariaoliveira.entity.Agendamento;



@Component 
public class AgendamentoMapper {

    private final BarbeariaProperties barbeariaProperties; 

    public AgendamentoMapper(BarbeariaProperties barbeariaProperties){
        this.barbeariaProperties = barbeariaProperties; 
    }

    public AgendamentoResponse toResponse(Agendamento agendamento){
        ZoneId fuso = barbeariaProperties.fusoHorario();

        return new AgendamentoResponse(
                agendamento.getId(),
                agendamento.getServico().getNome(),
                agendamento.getBarbeiro().getNome(),
                agendamento.getNomeCliente(),
                LocalDateTime.ofInstant(agendamento.getDataHoraInicio(), fuso),
                LocalDateTime.ofInstant(agendamento.getDataHoraFim(), fuso),
                agendamento.getValorCobrado(),
                agendamento.getStatus()
        );
    }
}
