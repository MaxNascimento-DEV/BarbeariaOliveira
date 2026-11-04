package com.barbeariaoliveira.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barbeariaoliveira.dto.LoginRequest;
import com.barbeariaoliveira.dto.LoginResponse;
import com.barbeariaoliveira.dto.TrocarSenhaRequest;
import com.barbeariaoliveira.entity.Usuario;
import com.barbeariaoliveira.exception.CredenciaisInvalidasException;
import com.barbeariaoliveira.exception.RecursoNaoEncontradoException;
import com.barbeariaoliveira.exception.RegraNegocioException;
import com.barbeariaoliveira.repository.UsuarioRepository;

@Service
public class AuthService {

    private static final String MSG_INVALIDO = "E-mail ou senha inválidos.";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.email().strip())
                .orElseThrow(() -> new CredenciaisInvalidasException(MSG_INVALIDO));

        if (!usuario.getAtivo() || !passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new CredenciaisInvalidasException(MSG_INVALIDO);
        }

        return jwtService.gerarToken(usuario);
    }

    /** Troca a própria senha. Devolve um token novo (já sem a obrigação de trocar). */
    @Transactional
    public LoginResponse trocarSenha(UsuarioLogado logado, TrocarSenhaRequest request) {
        Usuario usuario = usuarioRepository.findById(logado.id())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));

        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenhaHash())) {
            throw new RegraNegocioException("A senha atual está incorreta.");
        }
        if (passwordEncoder.matches(request.novaSenha(), usuario.getSenhaHash())) {
            throw new RegraNegocioException("A nova senha precisa ser diferente da atual.");
        }

        usuario.setSenhaHash(passwordEncoder.encode(request.novaSenha()));
        usuario.setTrocarSenha(false);
        usuarioRepository.save(usuario);
        return jwtService.gerarToken(usuario);
    }
}
