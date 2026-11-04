package com.barbeariaoliveira.dto;

import java.math.BigDecimal;

public record ServicoResponse(

    Long id,
    String nome,
    BigDecimal valor,
    Integer duracaoMinutos,
    String descricao,
    String icone,
    String categoria,
    Boolean ativo

) {

}
