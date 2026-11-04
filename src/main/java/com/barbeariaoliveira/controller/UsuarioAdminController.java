package com.barbeariaoliveira.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.barbeariaoliveira.dto.RedefinirSenhaRequest;
import com.barbeariaoliveira.dto.UsuarioAtualizacaoRequest;
import com.barbeariaoliveira.dto.UsuarioRequest;
import com.barbeariaoliveira.dto.UsuarioResponse;
import com.barbeariaoliveira.security.UsuarioLogado;
import com.barbeariaoliveira.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

    private final UsuarioService usuarioService;

    public UsuarioAdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody UsuarioRequest request) {
        return usuarioService.criar(request);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable Long id,
                                     @Valid @RequestBody UsuarioAtualizacaoRequest request,
                                     @AuthenticationPrincipal UsuarioLogado logado) {
        return usuarioService.atualizar(id, request, logado);
    }

    @PatchMapping("/{id}/ativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(@PathVariable Long id) {
        usuarioService.ativar(id);
    }

    @PatchMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id, @AuthenticationPrincipal UsuarioLogado logado) {
        usuarioService.desativar(id, logado);
    }

    @PatchMapping("/{id}/redefinir-senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void redefinirSenha(@PathVariable Long id, @Valid @RequestBody RedefinirSenhaRequest request) {
        usuarioService.redefinirSenha(id, request.senhaProvisoria());
    }
}
