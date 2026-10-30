package com.barbeariaoliveira.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgendamentoRequest(

    @NotNull(message = "A escolha do serviço é obrigatória.")
    Long servicoId,

    @NotNull(message = "A escolha do barbeiro é obrigatória.")
    Long barbeiroId,

    @NotNull(message = "O horário é obrigatório.")
    LocalDateTime dataHoraInicio,

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 150, message =  "O nome deve ter entre 3 e 150 caracteres.")
    String nomeCliente,

    @NotBlank(message = "O telefone é obrigatório.")
    @Pattern(regexp = "^[0-9()\\s+-]{10,20}$", message = "Telefone inválido.")
    String telefoneCliente,

    // Token gerado pelo reCAPTCHA v3 no navegador (opcional enquanto o reCAPTCHA estiver desligado)
    String recaptchaToken

) {}
