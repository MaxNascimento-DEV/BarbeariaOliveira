package com.barbeariaoliveira.controller;

import java.time.*;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.barbeariaoliveira.dto.AgendamentoRequest;
import com.barbeariaoliveira.dto.AgendamentoResponse;
import com.barbeariaoliveira.service.AgendamentoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/public/agendamentos")
public class AgendamentoPublicController {

    private final AgendamentoService agendamentoService;

    public AgendamentoPublicController(AgendamentoService agendamentoService){
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoRequest request){
        return agendamentoService.criar(request); 
    }

    @GetMapping("/disponibilidade")
    public List<LocalTime> listarDisponiveis(@RequestParam Long barbeiroId, @RequestParam Long servicoId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
    return agendamentoService.listarDisponiveis(barbeiroId, servicoId, data);
}

}
