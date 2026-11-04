package com.barbeariaoliveira.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import com.barbeariaoliveira.enums.Perfil;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "senha_hash", nullable = false, length = 100)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil;

    // Preenchido só quando perfil = BARBEIRO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbeiro_id")
    private Barbeiro barbeiro;

    // true = senha provisória: precisa trocar antes de usar o painel
    @Builder.Default
    @Column(name = "trocar_senha", nullable = false)
    private Boolean trocarSenha = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm;
}
