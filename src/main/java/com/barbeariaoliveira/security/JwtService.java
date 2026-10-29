package com.barbeariaoliveira.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import com.barbeariaoliveira.config.JwtProperties;
import com.barbeariaoliveira.dto.LoginResponse;
import com.barbeariaoliveira.entity.UsuarioAdmin;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey chave;
    private final Duration expiracao;

    public JwtService(JwtProperties jwtProperties) {
        this.chave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.segredo()));
        this.expiracao = jwtProperties.expiracao();
    }

    public LoginResponse gerarToken(UsuarioAdmin admin) {
        Instant agora = Instant.now();
        Instant expiraEm = agora.plus(expiracao);

        String token = Jwts.builder()
                .subject(admin.getId().toString())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiraEm))
                .signWith(chave)
                .compact();

        return new LoginResponse(token, expiraEm);
    }

    public Optional<Long> extrairAdminId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return Optional.of(Long.valueOf(subject));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}