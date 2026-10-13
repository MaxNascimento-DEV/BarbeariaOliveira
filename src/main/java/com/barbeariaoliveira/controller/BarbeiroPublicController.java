package com.barbeariaoliveira.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbeariaoliveira.dto.BarbeiroPublicResponse;
import com.barbeariaoliveira.service.BarbeiroService;

@RestController
@RequestMapping("/api/public/barbeiros")
public class BarbeiroPublicController {
    private final BarbeiroService barbeiroService;

    public BarbeiroPublicController(BarbeiroService barbeiroService){
        this.barbeiroService = barbeiroService; 
    }

    @GetMapping
    public List<BarbeiroPublicResponse> listarAtivos(){
        return barbeiroService.listarAtivos(); 
    }

}
