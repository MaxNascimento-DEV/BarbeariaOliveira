package com.barbeariaoliveira.dto;

public record BarbeiroResponse(

    Long id,
    String nome,
    String especialidade,
    String telefone,
    String fotoUrl,
    Boolean ativo
) {}
