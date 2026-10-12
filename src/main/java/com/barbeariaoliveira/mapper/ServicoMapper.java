package com.barbeariaoliveira.mapper;

import org.springframework.stereotype.Component;

import com.barbeariaoliveira.dto.ServicoRequest;
import com.barbeariaoliveira.dto.ServicoResponse;
import com.barbeariaoliveira.entity.Servico;

@Component 
public class ServicoMapper {
    public Servico toEntity(ServicoRequest request){
        return Servico.builder()
                .nome(request.nome())
                .valor(request.valor())
                .duracaoMinutos(request.duracaoMinutos())
                .build();
            
    }

    public ServicoResponse toResponse(Servico servico){
        return new ServicoResponse(
                    servico.getId(),
                    servico.getNome(),
                    servico.getValor(),
                    servico.getDuracaoMinutos(),
                    servico.getAtivo()
        );
    }
}
