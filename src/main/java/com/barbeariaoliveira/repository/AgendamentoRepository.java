package com.barbeariaoliveira.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.barbeariaoliveira.entity.Agendamento;
import com.barbeariaoliveira.enums.StatusAgendamento;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

        boolean existsByTelefoneClienteAndStatusIn(String telefoneCliente, Collection<StatusAgendamento> status);

        @Query("""
        SELECT a
        FROM Agendamento a
        JOIN FETCH a.servico
        JOIN FETCH a.barbeiro
        WHERE a.dataHoraInicio >= :inicio
          AND a.dataHoraInicio < :fim
        ORDER BY a.dataHoraInicio
        """)
      List<Agendamento> buscarPorPeriodo(@Param("inicio") Instant inicio, @Param("fim") Instant fim);

      List<Agendamento> findByStatusAndDataHoraInicioLessThanEqual(StatusAgendamento status, Instant limite);       
      
      List<Agendamento> findByStatusAndDataHoraFimLessThanEqual(StatusAgendamento status, Instant limite);

      @Query("""
        SELECT a
        FROM Agendamento a
        WHERE a.barbeiro.id = :barbeiroId 
          AND a.status IN :status
          AND a.dataHoraInicio >= :inicio
          AND a.dataHoraInicio < :fim
          ORDER BY a.dataHoraInicio
        """)
      List<Agendamento> buscarOcupadosNoPeriodo(@Param("barbeiroId") Long barbeiroId, @Param("status") Collection<StatusAgendamento> status, @Param("inicio") Instant inicio, @Param("fim") Instant fim);
}
