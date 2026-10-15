package com.barbeariaoliveira.dto;

import java.time.LocalTime;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

public record HorarioTrabalhoRequest(

    @NotNull(message = "A hora de abertura é obrigatoria.")
    LocalTime horaAbertura,

    @NotNull(message = "A hora de fechamento é obrigatoria.")
    LocalTime horaFechamento,

    LocalTime almocoInicio,

    LocalTime almocoFim

) {
    @AssertTrue(message = "O fechamento deve ser depois da abertura.")
    public boolean isExpedienteValido(){
        if(horaAbertura == null || horaFechamento == null){
            return true;
        } 
        return horaFechamento.isAfter(horaAbertura); 
    }

    @AssertTrue(message = "Informe o início e o fim do almoço, ou deixe os dois vazios.")
    public boolean isAlmocoCompleto(){
        return (almocoInicio == null) == (almocoFim == null); 
    }

    @AssertTrue(message = "O almoço tem que ser dentro do horário de expediente")
    public boolean isAlmocoDentroDoExpediente(){
           if (horaAbertura == null || horaFechamento == null || almocoInicio == null || almocoFim == null) {
                return true; 
           }
           return !almocoInicio.isBefore(horaAbertura) && !almocoFim.isAfter(horaFechamento) && almocoFim.isAfter(almocoInicio);
        }
    
}
