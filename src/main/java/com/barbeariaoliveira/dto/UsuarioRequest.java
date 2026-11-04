package com.barbeariaoliveira.dto;

import com.barbeariaoliveira.enums.Perfil;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Criação de login pelo admin. A senha é provisória: a pessoa troca no primeiro acesso. */
public record UsuarioRequest(

        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
        String email,

        @NotNull(message = "Escolha o perfil.")
        Perfil perfil,

        // Obrigatório quando perfil = BARBEIRO
        Long barbeiroId,

        @NotBlank(message = "Informe a senha provisória.")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
        String senhaProvisoria) {
}
