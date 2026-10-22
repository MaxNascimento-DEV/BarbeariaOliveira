package com.barbeariaoliveira.enums;

import java.time.DayOfWeek;

public enum DiaSemana {
    DOMINGO,
    SEGUNDA,
    TERCA,
    QUARTA,
    QUINTA,
    SEXTA,
    SABADO;

    public static DiaSemana de(DayOfWeek dia){
        return switch(dia){
            case SUNDAY -> DOMINGO; 
            case MONDAY -> SEGUNDA;
            case TUESDAY -> TERCA; 
            case WEDNESDAY -> QUARTA;
            case THURSDAY -> QUINTA;
            case FRIDAY -> SEXTA;
            case SATURDAY -> SABADO;
        };
    }
}
