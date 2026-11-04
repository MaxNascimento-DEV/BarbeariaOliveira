package com.barbeariaoliveira.dto;

import java.time.Instant;

import com.barbeariaoliveira.enums.Perfil;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        Long barbeiroId,
        String nomeBarbeiro,
        Boolean ativo,
        Boolean trocarSenha,
        Instant criadoEm) {
}
