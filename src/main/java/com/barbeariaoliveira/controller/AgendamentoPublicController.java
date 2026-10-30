package com.barbeariaoliveira.controller;

import java.time.*;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.barbeariaoliveira.dto.AgendamentoRequest;
import com.barbeariaoliveira.dto.AgendamentoResponse;
import com.barbeariaoliveira.security.RecaptchaService;
import com.barbeariaoliveira.service.AgendamentoService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/api/public/agendamentos")
public class AgendamentoPublicController {

    private static final String ACAO_RECAPTCHA = "agendar";

    private final AgendamentoService agendamentoService;
    private final RecaptchaService recaptchaService;

    public AgendamentoPublicController(AgendamentoService agendamentoService,
                                       RecaptchaService recaptchaService) {
        this.agendamentoService = agendamentoService;
        this.recaptchaService = recaptchaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoRequest request){
        // Verificado ANTES do Service: a chamada ao Google não pode acontecer com o lock do barbeiro preso
        recaptchaService.verificar(request.recaptchaToken(), ACAO_RECAPTCHA);
        return agendamentoService.criar(request);
    }

    @GetMapping("/disponibilidade")
    public List<LocalTime> listarDisponiveis(@RequestParam Long barbeiroId, @RequestParam Long servicoId,@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
    return agendamentoService.listarDisponiveis(barbeiroId, servicoId, data);
}

}
