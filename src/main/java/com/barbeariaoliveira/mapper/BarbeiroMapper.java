package com.barbeariaoliveira.mapper;

import org.springframework.stereotype.Component;

import com.barbeariaoliveira.dto.BarbeiroPublicResponse;
import com.barbeariaoliveira.dto.BarbeiroRequest;
import com.barbeariaoliveira.dto.BarbeiroResponse;
import com.barbeariaoliveira.entity.Barbeiro;

@Component 
public class BarbeiroMapper {

    public Barbeiro toEntity(BarbeiroRequest request){
        return Barbeiro.builder()
        .nome(request.nome())
        .especialidade(request.especialidade())
        .telefone(request.telefone())
        .build();
    }

    public BarbeiroResponse toResponse(Barbeiro barbeiro){
        return new BarbeiroResponse(
            barbeiro.getId(),
            barbeiro.getNome(),
            barbeiro.getEspecialidade(),
            barbeiro.getTelefone(),
            barbeiro.getAtivo()
        );
    }

public BarbeiroPublicResponse toPublicResponse(Barbeiro barbeiro) {
    return new BarbeiroPublicResponse(
            barbeiro.getId(),
            barbeiro.getNome(),
            barbeiro.getEspecialidade()
    );
}
}
