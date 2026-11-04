package com.barbeariaoliveira.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.barbeariaoliveira.entity.Usuario;
import com.barbeariaoliveira.enums.Perfil;
import com.barbeariaoliveira.security.UsuarioLogado;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    boolean existsByBarbeiroId(Long barbeiroId);

    boolean existsByBarbeiroIdAndIdNot(Long barbeiroId, Long id);

    long countByPerfilAndAtivoTrue(Perfil perfil);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.barbeiro ORDER BY u.nome")
    List<Usuario> listarTodosComBarbeiro();

    // Usado a cada requisição pelo filtro JWT: lê o estado ATUAL do usuário
    // (desativado ou com senha redefinida perde o acesso na hora)
    @Query("""
            SELECT new com.barbeariaoliveira.security.UsuarioLogado(u.id, u.perfil, b.id, u.trocarSenha)
            FROM Usuario u
            LEFT JOIN u.barbeiro b
            WHERE u.id = :id
              AND u.ativo = true
            """)
    Optional<UsuarioLogado> buscarLogado(@Param("id") Long id);
}
