package com.barbeariaoliveira.controller;

import java.time.LocalDate;
import java.util.List;

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

    @PatchMapping("/{id}/confirmar")
    public AgendamentoResponse confirmar(@PathVariable Long id){
        return agendamentoService.confirmar(id); 
    }

    @PatchMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@PathVariable Long id){
        return  agendamentoService.cancelar(id); 
    }





}
