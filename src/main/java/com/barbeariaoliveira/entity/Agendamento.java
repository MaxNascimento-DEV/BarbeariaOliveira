package com.barbeariaoliveira.entity;


import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import com.barbeariaoliveira.enums.StatusAgendamento;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "agendamento")
public class Agendamento {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "barbeiro_id")
    private Barbeiro barbeiro; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servico_id")
    private Servico servico; 

    @Column(name = "nome_cliente", nullable = false, length = 150)
    private String nomeCliente; 

    @Column(name = "telefone_cliente", nullable = false, length = 20)
    private String telefoneCliente; 

    
    @Column(name = "data_hora_inicio", nullable = false)
    private Instant dataHoraInicio;

    
    @Column(name = "data_hora_fim", nullable = false)
    private Instant dataHoraFim; 

    @Column(name = "valor_cobrado",nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCobrado;   

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusAgendamento status = StatusAgendamento.AGENDADO; 

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private Instant criadoEm; 

}
