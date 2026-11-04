package com.barbeariaoliveira.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbeariaoliveira.dto.LoginResponse;
import com.barbeariaoliveira.dto.TrocarSenhaRequest;
import com.barbeariaoliveira.security.AuthService;
import com.barbeariaoliveira.security.UsuarioLogado;

import jakarta.validation.Valid;

/** Ações do próprio usuário logado (admin ou barbeiro). */
@RestController
@RequestMapping("/api/conta")
public class ContaController {

    private final AuthService authService;

    public ContaController(AuthService authService) {
        this.authService = authService;
    }

    @PutMapping("/senha")
    public LoginResponse trocarSenha(@AuthenticationPrincipal UsuarioLogado logado,
                                     @Valid @RequestBody TrocarSenhaRequest request) {
        return authService.trocarSenha(logado, request);
    }
}
