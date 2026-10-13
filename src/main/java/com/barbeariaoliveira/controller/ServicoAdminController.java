package com.barbeariaoliveira.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.barbeariaoliveira.dto.ServicoRequest;
import com.barbeariaoliveira.dto.ServicoResponse;
import com.barbeariaoliveira.service.ServicoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/servicos")
public class ServicoAdminController {

    private final ServicoService servicoService;

    public ServicoAdminController(ServicoService servicoService){
        this.servicoService = servicoService;
    }

    @GetMapping
    public List<ServicoResponse> listarTodos(){
        return servicoService.listarTodos(); 
    }

    @GetMapping("/{id}")
    public ServicoResponse buscarPorId(@PathVariable Long id){
        return servicoService.buscarPorId(id); 
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServicoResponse criar(@Valid @RequestBody ServicoRequest request){
        return servicoService.criar(request); 
    }

    @PutMapping("/{id}")
    public ServicoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ServicoRequest request){
        return servicoService.atualizar(id, request); 
    }

    @PatchMapping("{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(@PathVariable Long id){
         servicoService.ativar(id);
    }

    @PatchMapping("{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id ){
        servicoService.desativar(id);
    }

}
