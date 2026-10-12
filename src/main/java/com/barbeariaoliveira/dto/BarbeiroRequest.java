package com.barbeariaoliveira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BarbeiroRequest(


    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 150, message =  "O nome deve ter entre 3 e 150 caracteres.")
    String nome,

    @Size(max = 100, message = "O nome deve ter entre 3 e 100 caracteres." )
    String especialidade,

    @NotBlank(message = "O telefone é obrigatorio")
    @Pattern(regexp = "^[0-9()\\s+-]{10,20}$", message = "Telefone inválido.")
    String telefone 
) {}
