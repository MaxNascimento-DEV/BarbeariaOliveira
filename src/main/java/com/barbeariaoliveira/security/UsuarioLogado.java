package com.barbeariaoliveira.security;

import com.barbeariaoliveira.enums.Perfil;

/**
 * Quem está fazendo a requisição (vira o "principal" do Spring Security).
 * Nos controllers: @AuthenticationPrincipal UsuarioLogado usuario
 */
public record UsuarioLogado(Long id, Perfil perfil, Long barbeiroId, Boolean trocarSenha) {

    public boolean precisaTrocarSenha() {
        return Boolean.TRUE.equals(trocarSenha);
    }
}
