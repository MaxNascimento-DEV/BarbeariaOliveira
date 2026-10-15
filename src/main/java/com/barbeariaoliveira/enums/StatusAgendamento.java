package com.barbeariaoliveira.enums;

import java.util.Set;

public enum StatusAgendamento {
    AGENDADO,
    CONFIRMADO,
    EM_ANDAMENTO,
    CONCLUIDO,
    CANCELADO;

    public boolean podeMudarPara(StatusAgendamento novo) {
        return switch (this) {
            case AGENDADO     -> Set.of(CONFIRMADO, CANCELADO).contains(novo);
            case CONFIRMADO   -> Set.of(EM_ANDAMENTO, CANCELADO).contains(novo);
            case EM_ANDAMENTO -> Set.of(CONCLUIDO, CANCELADO).contains(novo);
            case CONCLUIDO, CANCELADO -> false;
        };
    }
}
