package com.barbeariaoliveira.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.barbeariaoliveira.dto.LoginRequest;
import com.barbeariaoliveira.dto.LoginResponse;
import com.barbeariaoliveira.entity.UsuarioAdmin;
import com.barbeariaoliveira.exception.CredenciaisInvalidasException;
import com.barbeariaoliveira.repository.UsuarioAdminRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioAdminRepository usuarioAdminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioAdminRepository usuarioAdminRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioAdminRepository = usuarioAdminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UsuarioAdmin admin = usuarioAdminRepository.findByEmailIgnoreCase(request.email().strip())
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos."));

        if (!admin.getAtivo() || !passwordEncoder.matches(request.senha(), admin.getSenhaHash())) {
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos.");
        }

        return jwtService.gerarToken(admin);
    }

}
