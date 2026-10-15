package com.barbeariaoliveira.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.barbeariaoliveira.entity.HorarioTrabalho;
import com.barbeariaoliveira.enums.DiaSemana;


public interface HorarioTrabalhoRepository extends JpaRepository<HorarioTrabalho, Long> {

    List<HorarioTrabalho> findByBarbeiroId(Long barbeiroId); 

    Optional<HorarioTrabalho> findByBarbeiroIdAndDiaSemana(Long barbeiroId, DiaSemana diaSemana);
 

}
