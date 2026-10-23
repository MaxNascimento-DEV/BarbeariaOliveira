package com.barbeariaoliveira.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.barbeariaoliveira.service.AgendamentoService;

@Component
public class AgendamentoScheduler {

    private final AgendamentoService agendamentoService;

    public AgendamentoScheduler(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @Scheduled(fixedDelay = 60_000)
    public void executarTransicoesAutomaticas() {
        agendamentoService.processarTransicoesAutomaticas();
    }
}