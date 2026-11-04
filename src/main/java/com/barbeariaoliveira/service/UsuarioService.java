package com.barbeariaoliveira.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barbeariaoliveira.dto.UsuarioAtualizacaoRequest;
import com.barbeariaoliveira.dto.UsuarioRequest;
import com.barbeariaoliveira.dto.UsuarioResponse;
import com.barbeariaoliveira.entity.Barbeiro;
import com.barbeariaoliveira.entity.Usuario;
import com.barbeariaoliveira.enums.Perfil;
import com.barbeariaoliveira.exception.RecursoNaoEncontradoException;
import com.barbeariaoliveira.exception.RegraNegocioException;
import com.barbeariaoliveira.repository.BarbeiroRepository;
import com.barbeariaoliveira.repository.UsuarioRepository;
import com.barbeariaoliveira.security.UsuarioLogado;

/** Gestão de logins pelo admin (tela "Usuários" do painel). */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BarbeiroRepository barbeiroRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.barbeiroRepository = barbeiroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        List<UsuarioResponse> respostas = new ArrayList<>();
        for (Usuario usuario : usuarioRepository.listarTodosComBarbeiro()) {
            respostas.add(toResponse(usuario));
        }
        return respostas;
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        String email = request.email().strip().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail.");
        }
        Barbeiro barbeiro = resolverBarbeiro(request.perfil(), request.barbeiroId(), null);

        Usuario usuario = Usuario.builder()
                .nome(request.nome().strip())
                .email(email)
                .senhaHash(passwordEncoder.encode(request.senhaProvisoria()))
                .perfil(request.perfil())
                .barbeiro(barbeiro)
                .trocarSenha(true) // senha provisória: troca no primeiro acesso
                .build();

        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioAtualizacaoRequest request, UsuarioLogado logado) {
        Usuario usuario = buscar(id);
        String email = request.email().strip().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RegraNegocioException("Já existe um usuário com esse e-mail.");
        }
        // Não deixar a barbearia sem nenhum admin (nem o admin se rebaixar sozinho)
        if (usuario.getPerfil() == Perfil.ADMIN && request.perfil() != Perfil.ADMIN) {
            if (usuario.getId().equals(logado.id())) {
                throw new RegraNegocioException("Você não pode remover o seu próprio perfil de admin.");
            }
            garantirOutroAdminAtivo(usuario);
        }

        usuario.setNome(request.nome().strip());
        usuario.setEmail(email);
        usuario.setPerfil(request.perfil());
        usuario.setBarbeiro(resolverBarbeiro(request.perfil(), request.barbeiroId(), id));
        return toResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public void ativar(Long id) {
        Usuario usuario = buscar(id);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void desativar(Long id, UsuarioLogado logado) {
        Usuario usuario = buscar(id);
        if (usuario.getId().equals(logado.id())) {
            throw new RegraNegocioException("Você não pode desativar o seu próprio acesso.");
        }
        if (usuario.getPerfil() == Perfil.ADMIN) {
            garantirOutroAdminAtivo(usuario);
        }
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
    }

    /** Admin define uma nova senha provisória (ex.: barbeiro esqueceu a dele). */
    @Transactional
    public void redefinirSenha(Long id, String senhaProvisoria) {
        Usuario usuario = buscar(id);
        usuario.setSenhaHash(passwordEncoder.encode(senhaProvisoria));
        usuario.setTrocarSenha(true);
        usuarioRepository.save(usuario);
    }

    // ---------------- auxiliares ----------------

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    private void garantirOutroAdminAtivo(Usuario usuario) {
        long adminsAtivos = usuarioRepository.countByPerfilAndAtivoTrue(Perfil.ADMIN);
        long contandoEste = Boolean.TRUE.equals(usuario.getAtivo()) ? 1 : 0;
        if (adminsAtivos - contandoEste < 1) {
            throw new RegraNegocioException("A barbearia precisa ter pelo menos um admin ativo.");
        }
    }

    /** BARBEIRO precisa de um barbeiro (sem outro login); ADMIN não tem barbeiro. */
    private Barbeiro resolverBarbeiro(Perfil perfil, Long barbeiroId, Long usuarioId) {
        if (perfil != Perfil.BARBEIRO) {
            return null;
        }
        if (barbeiroId == null) {
            throw new RegraNegocioException("Escolha qual barbeiro é dono deste login.");
        }
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Barbeiro não encontrado."));
        boolean jaTemLogin = usuarioId == null
                ? usuarioRepository.existsByBarbeiroId(barbeiroId)
                : usuarioRepository.existsByBarbeiroIdAndIdNot(barbeiroId, usuarioId);
        if (jaTemLogin) {
            throw new RegraNegocioException("Esse barbeiro já tem um login.");
        }
        return barbeiro;
    }

    private UsuarioResponse toResponse(Usuario u) {
        Barbeiro b = u.getBarbeiro();
        return new UsuarioResponse(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPerfil(),
                b == null ? null : b.getId(),
                b == null ? null : b.getNome(),
                u.getAtivo(),
                u.getTrocarSenha(),
                u.getCriadoEm());
    }
}
