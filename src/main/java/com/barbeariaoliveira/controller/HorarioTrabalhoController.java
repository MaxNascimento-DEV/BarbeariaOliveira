package com.barbeariaoliveira.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.barbeariaoliveira.dto.HorarioTrabalhoRequest;
import com.barbeariaoliveira.dto.HorarioTrabalhoResponse;
import com.barbeariaoliveira.enums.DiaSemana;
import com.barbeariaoliveira.service.HorarioTrabalhoService;
import jakarta.validation.Valid;
import java.util.List;


@RestController 
@RequestMapping("/api/admin/barbeiros/{barbeiroId}/horarios")
public class HorarioTrabalhoController {

    private final HorarioTrabalhoService horarioTrabalhoService;

    public HorarioTrabalhoController(HorarioTrabalhoService horarioTrabalhoService){
        this.horarioTrabalhoService = horarioTrabalhoService; 
    }

    @GetMapping
    public List<HorarioTrabalhoResponse> listar(@PathVariable Long barbeiroId){
        return horarioTrabalhoService.listar(barbeiroId); 
    }

    @PutMapping("/{dia}")
    public HorarioTrabalhoResponse definir(@PathVariable Long barbeiroId, @PathVariable DiaSemana dia, @Valid @RequestBody HorarioTrabalhoRequest request){
        return horarioTrabalhoService.definir(barbeiroId, dia, request);
    }
    
    @DeleteMapping("/{dia}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long barbeiroId, @PathVariable DiaSemana dia){
         horarioTrabalhoService.remover(barbeiroId, dia);
    }
}
