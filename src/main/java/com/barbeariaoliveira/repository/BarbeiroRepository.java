package com.barbeariaoliveira.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.barbeariaoliveira.entity.Barbeiro;
import jakarta.persistence.LockModeType;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long> {

    List<Barbeiro> findByAtivoTrueOrderByNomeAsc();

    List<Barbeiro> findAllByOrderByNomeAsc();

    boolean existsByTelefoneAndIdNot(String telefone, Long id);

    boolean existsByTelefone(String telefone);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Barbeiro b WHERE b.id = :id")
    Optional<Barbeiro> buscarComLock(@Param("id") Long id);
}
