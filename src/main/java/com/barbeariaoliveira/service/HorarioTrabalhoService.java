package com.barbeariaoliveira.service;

import com.barbeariaoliveira.repository.BarbeiroRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barbeariaoliveira.dto.HorarioTrabalhoRequest;
import com.barbeariaoliveira.dto.HorarioTrabalhoResponse;
import com.barbeariaoliveira.entity.Barbeiro;
import com.barbeariaoliveira.entity.HorarioTrabalho;
import com.barbeariaoliveira.enums.DiaSemana;
import com.barbeariaoliveira.exception.RecursoNaoEncontradoException;
import com.barbeariaoliveira.mapper.HorarioTrabalhoMapper;
import com.barbeariaoliveira.repository.HorarioTrabalhoRepository;


@Service 
public class HorarioTrabalhoService {
    private final BarbeiroRepository barbeiroRepository;
    private final HorarioTrabalhoRepository horarioTrabalhoRepository;
    private final HorarioTrabalhoMapper horarioTrabalhoMapper;

    public HorarioTrabalhoService(HorarioTrabalhoMapper horarioTrabalhoMapper, HorarioTrabalhoRepository horarioTrabalhoRepository, BarbeiroRepository barbeiroRepository){
        this.horarioTrabalhoMapper = horarioTrabalhoMapper;
        this.horarioTrabalhoRepository = horarioTrabalhoRepository;
        this.barbeiroRepository = barbeiroRepository;
    }

    @Transactional(readOnly = true)
    public List<HorarioTrabalhoResponse> listar(Long barbeiroId){
        if (!barbeiroRepository.existsById(barbeiroId)){
            throw new RecursoNaoEncontradoException("Barbeiro não encontrado.");
        }

        List<HorarioTrabalho> horarios = horarioTrabalhoRepository.findByBarbeiroId(barbeiroId);
        horarios.sort(Comparator.comparing(HorarioTrabalho::getDiaSemana));

        List<HorarioTrabalhoResponse> respostas = new ArrayList<>(); 
        for(HorarioTrabalho horario : horarios){
            respostas.add(horarioTrabalhoMapper.toResponse(horario));
        }
        return respostas;
    }

    @Transactional
    public HorarioTrabalhoResponse definir(Long barbeiroId, DiaSemana dia, HorarioTrabalhoRequest request){
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId).orElseThrow(() -> new RecursoNaoEncontradoException("Barbeiro não encontrado"));

        HorarioTrabalho horario = horarioTrabalhoRepository.findByBarbeiroIdAndDiaSemana(barbeiroId, dia)
        .orElseGet(() -> HorarioTrabalho.builder().barbeiro(barbeiro).diaSemana(dia).build()); 

        horarioTrabalhoMapper.atualizarEntidade(request, horario);

        HorarioTrabalho salvo = horarioTrabalhoRepository.save(horario); 

        return horarioTrabalhoMapper.toResponse(salvo); 
    }

    @Transactional
    public void remover(Long barbeiroId, DiaSemana dia){
        HorarioTrabalho horario = horarioTrabalhoRepository.findByBarbeiroIdAndDiaSemana(barbeiroId, dia).orElseThrow(() -> new RecursoNaoEncontradoException("Horário não encontrado.")); 

        horarioTrabalhoRepository.delete(horario);
    }
        


}
