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

      // Painel: agendamentos criados depois do último que o navegador já viu
      @Query("""
        SELECT a
        FROM Agendamento a
        JOIN FETCH a.servico
        JOIN FETCH a.barbeiro
        WHERE a.id > :aposId
        ORDER BY a.id
        """)
      List<Agendamento> buscarCriadosApos(@Param("aposId") Long aposId);

      @Query("SELECT COALESCE(MAX(a.id), 0) FROM Agendamento a")
      Long buscarUltimoId();

      // ---- Painel do barbeiro (sempre filtrado pelo barbeiro do login) ----

      @Query("""
        SELECT a
        FROM Agendamento a
        JOIN FETCH a.servico
        JOIN FETCH a.barbeiro
        WHERE a.barbeiro.id = :barbeiroId
          AND a.dataHoraInicio >= :inicio
          AND a.dataHoraInicio < :fim
        ORDER BY a.dataHoraInicio
        """)
      List<Agendamento> buscarPorPeriodoDoBarbeiro(@Param("barbeiroId") Long barbeiroId,
                                                   @Param("inicio") Instant inicio, @Param("fim") Instant fim);

      @Query("""
        SELECT a
        FROM Agendamento a
        JOIN FETCH a.servico
        JOIN FETCH a.barbeiro
        WHERE a.id > :aposId
          AND a.barbeiro.id = :barbeiroId
        ORDER BY a.id
        """)
      List<Agendamento> buscarCriadosAposDoBarbeiro(@Param("aposId") Long aposId, @Param("barbeiroId") Long barbeiroId);

      @Query("SELECT COALESCE(MAX(a.id), 0) FROM Agendamento a WHERE a.barbeiro.id = :barbeiroId")
      Long buscarUltimoIdDoBarbeiro(@Param("barbeiroId") Long barbeiroId);

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
