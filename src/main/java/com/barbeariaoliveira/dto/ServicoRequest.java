package com.barbeariaoliveira.dto;
import java.math.BigDecimal;
import jakarta.validation.constraints.*;


public record ServicoRequest(


    @NotBlank(message = "O nome é obrigatorio.")
    @Size(min = 3, max = 150, message = "O nome deve ter entre 3 e 150 caracteres." )
    String nome,

    @NotNull(message = "O valor é obrigatorio.")
    @Positive(message = "O valor deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O preço deve ter no máximo 2 casas decimais.")
    BigDecimal valor,

    @NotNull(message = "A duração é obrigatorio.")
    @Positive(message = "Duração tem que ser maior que zero.")
    Integer duracaoMinutos

) {}
