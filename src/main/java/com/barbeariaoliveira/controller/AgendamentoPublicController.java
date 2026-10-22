package com.barbeariaoliveira.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

}
