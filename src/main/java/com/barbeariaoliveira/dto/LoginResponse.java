package com.barbeariaoliveira.dto;

import java.time.Instant;

import com.barbeariaoliveira.enums.Perfil;

public record LoginResponse(
        String token,
        Instant expiraEm,
        String nome,
        String email,
        Perfil perfil,
        Long barbeiroId,
        Boolean trocarSenha) {
}
