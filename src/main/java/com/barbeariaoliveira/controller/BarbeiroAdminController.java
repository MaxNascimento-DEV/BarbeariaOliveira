package com.barbeariaoliveira.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.barbeariaoliveira.dto.BarbeiroRequest;
import com.barbeariaoliveira.dto.BarbeiroResponse;
import com.barbeariaoliveira.service.BarbeiroService;


@RestController 
@RequestMapping("/api/admin/barbeiros")
public class BarbeiroAdminController {
    private final BarbeiroService barbeiroService;

    public BarbeiroAdminController(BarbeiroService barbeiroService){
        this.barbeiroService = barbeiroService;
    }

    @GetMapping
    public List<BarbeiroResponse> listarTodos(){
        return barbeiroService.listarTodos(); 
    }

    @GetMapping("/{id}")
    public BarbeiroResponse buscarPorId(@PathVariable Long id){
        return barbeiroService.buscarPorId(id);
    }

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public BarbeiroResponse criar(@Valid @RequestBody BarbeiroRequest request){
        return barbeiroService.criar(request);
    }

    @PutMapping("/{id}")
    public BarbeiroResponse atualizar(@PathVariable Long id, @Valid @RequestBody BarbeiroRequest request){
        return barbeiroService.atualizar(id, request);
    }

    @PatchMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(@PathVariable Long id){
        barbeiroService.ativar(id);
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id){
        barbeiroService.desativar(id);
    }
}
