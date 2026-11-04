package com.barbeariaoliveira.mapper;

import org.springframework.stereotype.Component;

import com.barbeariaoliveira.dto.ServicoRequest;
import com.barbeariaoliveira.dto.ServicoResponse;
import com.barbeariaoliveira.entity.Servico;

@Component 
public class ServicoMapper {

    public static final String ICONE_PADRAO = "scissors";
    public static final String CATEGORIA_PADRAO = "cabelo";

    public static String textoOuNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    public static String textoOuPadrao(String texto, String padrao) {
        return (texto == null || texto.isBlank()) ? padrao : texto.trim();
    }

    public Servico toEntity(ServicoRequest request){
        return Servico.builder()
                .nome(request.nome())
                .valor(request.valor())
                .duracaoMinutos(request.duracaoMinutos())
                .descricao(textoOuNulo(request.descricao()))
                .icone(textoOuPadrao(request.icone(), ICONE_PADRAO))
                .categoria(textoOuPadrao(request.categoria(), CATEGORIA_PADRAO))
                .build();
            
    }

    public ServicoResponse toResponse(Servico servico){
        return new ServicoResponse(
                    servico.getId(),
                    servico.getNome(),
                    servico.getValor(),
                    servico.getDuracaoMinutos(),
                    servico.getDescricao(),
                    servico.getIcone(),
                    servico.getCategoria(),
                    servico.getAtivo()
        );
    }
}
