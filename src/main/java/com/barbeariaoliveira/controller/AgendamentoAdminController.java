package com.barbeariaoliveira.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import com.barbeariaoliveira.dto.AgendamentoResponse;
import com.barbeariaoliveira.service.AgendamentoService;

@RestController 
@RequestMapping("/api/admin/agendamentos")
public class AgendamentoAdminController {

    private final AgendamentoService agendamentoService;

    public AgendamentoAdminController(AgendamentoService agendamentoService){
        this.agendamentoService = agendamentoService; 
    }


    @GetMapping
    public List<AgendamentoResponse> listarPorData(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate data){
        return agendamentoService.listarPorData(data);
    }

    // Usado pelo painel para tocar o aviso de "novo agendamento"
    @GetMapping("/novos")
    public List<AgendamentoResponse> listarNovos(@RequestParam Long aposId) {
        return agendamentoService.listarCriadosApos(aposId);
    }

    @GetMapping("/ultimo-id")
    public Map<String, Long> ultimoId() {
        return Map.of("id", agendamentoService.buscarUltimoId());
    }

    @PatchMapping("/{id}/confirmar")
    public AgendamentoResponse confirmar(@PathVariable Long id){
        return agendamentoService.confirmar(id); 
    }

    @PatchMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id){
        return  agendamentoService.cancelar(id); 
    }





}
