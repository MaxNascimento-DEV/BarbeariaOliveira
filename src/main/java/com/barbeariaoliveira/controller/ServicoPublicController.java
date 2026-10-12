package com.barbeariaoliveira.controller;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.barbeariaoliveira.dto.ServicoResponse;
import com.barbeariaoliveira.service.ServicoService;

@RestController 
@RequestMapping("/api/public/servicos")
public class ServicoPublicController {
        private final ServicoService servicoService; 

    public ServicoPublicController(ServicoService servicoService){
        this.servicoService = servicoService; 
    }


    @GetMapping
    public List<ServicoResponse> listarAtivos(){
        return servicoService.listarAtivos(); 
    }
}
