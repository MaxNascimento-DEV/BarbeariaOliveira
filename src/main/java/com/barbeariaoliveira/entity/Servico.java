package com.barbeariaoliveira.entity;

import java.math.BigDecimal;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "servico")
public class Servico {


    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor; 

    @Column(name = "duracao_minutos", nullable = false)
    private Integer duracaoMinutos; 

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true; 

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm; 


}
