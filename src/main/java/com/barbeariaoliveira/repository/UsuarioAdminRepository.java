package com.barbeariaoliveira.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbeariaoliveira.entity.UsuarioAdmin;

public interface UsuarioAdminRepository extends JpaRepository<UsuarioAdmin, Long> {

    Optional<UsuarioAdmin> findByEmailIgnoreCase(String email);
}
