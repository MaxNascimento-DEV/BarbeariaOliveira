package com.barbeariaoliveira.dto;

import java.time.LocalTime;
import com.barbeariaoliveira.enums.DiaSemana;

public record HorarioTrabalhoResponse(

    DiaSemana diaSemana,

    LocalTime horaAbertura,

    LocalTime horaFechamento,

    LocalTime almocoInicio,

    LocalTime almocoFim

) {}
