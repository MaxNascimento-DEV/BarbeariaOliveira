package com.barbeariaoliveira.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbeariaoliveira.entity.Barbeiro;

public interface BarbeiroRepository extends JpaRepository<Barbeiro, Long>{

    List<Barbeiro> findByAtivoTrueOrderByNomeAsc();
    List<Barbeiro> findAllByOrderByNomeAsc(); 
    boolean existsByTelefoneAndIdNot(String telefone, Long id); 
    boolean existsByTelefone(String telefone); 

}
