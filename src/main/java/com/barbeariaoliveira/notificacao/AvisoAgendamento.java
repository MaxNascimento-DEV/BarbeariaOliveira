package com.barbeariaoliveira.notificacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento publicado pelo AgendamentoService. Carrega só dados simples (sem entidades JPA),
 * porque é tratado DEPOIS do commit, em outra thread, quando a sessão do Hibernate já fechou.
 */
public record AvisoAgendamento(
        Tipo tipo,
        Long agendamentoId,
        String nomeCliente,
        String telefoneCliente,
        String nomeServico,
        BigDecimal valor,
        String nomeBarbeiro,
        String telegramChatIdBarbeiro,
        LocalDateTime inicio) {

    public enum Tipo {
        NOVO,
        CANCELADO_SEM_CONFIRMACAO
    }
}
