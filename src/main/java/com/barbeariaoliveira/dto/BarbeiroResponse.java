package com.barbeariaoliveira.dto;

public record BarbeiroResponse(

    Long id,
    String nome,
    String especialidade,
    String telefone,
    Boolean ativo
) {}
