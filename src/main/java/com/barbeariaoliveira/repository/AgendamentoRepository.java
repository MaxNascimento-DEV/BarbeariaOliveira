package com.barbeariaoliveira.repository;

import java.time.Instant;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.barbeariaoliveira.entity.Agendamento;
import com.barbeariaoliveira.enums.StatusAgendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("""
        SELECT COUNT(a) > 0
        FROM Agendamento a
        WHERE a.barbeiro.id = :barbeiroId
          AND a.status IN :statusQueOcupam
          AND a.dataHoraInicio < :fim
          AND a.dataHoraFim > :inicio
        """)
boolean existeConflito(@Param("barbeiroId") Long barbeiroId,
                       @Param("statusQueOcupam") Collection<StatusAgendamento> statusQueOcupam,
                       @Param("inicio") Instant inicio,
                       @Param("fim") Instant fim);


        boolean existsByTelefoneClienteAndStatusIn(String telefoneCliente, Collection<StatusAgendamento> status);
}
