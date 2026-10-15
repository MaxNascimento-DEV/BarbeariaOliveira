package com.barbeariaoliveira.mapper;

import org.springframework.stereotype.Component;

import com.barbeariaoliveira.dto.HorarioTrabalhoRequest;
import com.barbeariaoliveira.dto.HorarioTrabalhoResponse;
import com.barbeariaoliveira.entity.HorarioTrabalho;

@Component 
public class HorarioTrabalhoMapper {
    
    public void atualizarEntidade(HorarioTrabalhoRequest request, HorarioTrabalho horario){
        horario.setHoraAbertura(request.horaAbertura());
        horario.setHoraFechamento(request.horaFechamento());
        horario.setAlmocoInicio(request.almocoInicio());
        horario.setAlmocoFim(request.almocoFim());
    }

    public HorarioTrabalhoResponse toResponse(HorarioTrabalho horario){
        return new HorarioTrabalhoResponse(
            horario.getDiaSemana(),
            horario.getHoraAbertura(),
            horario.getHoraFechamento(),
            horario.getAlmocoInicio(),
            horario.getAlmocoFim()
        );
        
    }
}   
