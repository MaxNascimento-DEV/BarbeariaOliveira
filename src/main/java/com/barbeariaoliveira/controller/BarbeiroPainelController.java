package com.barbeariaoliveira.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.barbeariaoliveira.dto.AgendamentoResponse;
import com.barbeariaoliveira.dto.HorarioTrabalhoRequest;
import com.barbeariaoliveira.dto.HorarioTrabalhoResponse;
import com.barbeariaoliveira.enums.DiaSemana;
import com.barbeariaoliveira.security.UsuarioLogado;
import com.barbeariaoliveira.service.AgendamentoService;
import com.barbeariaoliveira.service.HorarioTrabalhoService;

import jakarta.validation.Valid;

/**
 * Painel do barbeiro: tudo aqui é filtrado pelo barbeiro do login
 * (o ID vem do token, nunca da URL — um barbeiro não consegue ver a agenda de outro).
 */
@RestController
@RequestMapping("/api/barbeiro")
public class BarbeiroPainelController {

    private final AgendamentoService agendamentoService;
    private final HorarioTrabalhoService horarioTrabalhoService;

    public BarbeiroPainelController(AgendamentoService agendamentoService,
                                    HorarioTrabalhoService horarioTrabalhoService) {
        this.agendamentoService = agendamentoService;
        this.horarioTrabalhoService = horarioTrabalhoService;
    }

    // ---------------- Agenda ----------------

    @GetMapping("/agendamentos")
    public List<AgendamentoResponse> listarPorData(@AuthenticationPrincipal UsuarioLogado logado,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return agendamentoService.listarPorDataDoBarbeiro(data, logado.barbeiroId());
    }

    @PatchMapping("/agendamentos/{id}/confirmar")
    public AgendamentoResponse confirmar(@AuthenticationPrincipal UsuarioLogado logado, @PathVariable Long id) {
        return agendamentoService.confirmarDoBarbeiro(id, logado.barbeiroId());
    }

    @PatchMapping("/agendamentos/{id}/cancelar")
    public AgendamentoResponse cancelar(@AuthenticationPrincipal UsuarioLogado logado, @PathVariable Long id) {
        return agendamentoService.cancelarDoBarbeiro(id, logado.barbeiroId());
    }

    @GetMapping("/agendamentos/novos")
    public List<AgendamentoResponse> listarNovos(@AuthenticationPrincipal UsuarioLogado logado,
                                                 @RequestParam Long aposId) {
        return agendamentoService.listarCriadosAposDoBarbeiro(aposId, logado.barbeiroId());
    }

    @GetMapping("/agendamentos/ultimo-id")
    public Map<String, Long> ultimoId(@AuthenticationPrincipal UsuarioLogado logado) {
        return Map.of("id", agendamentoService.buscarUltimoIdDoBarbeiro(logado.barbeiroId()));
    }

    // ---------------- Horários de trabalho ----------------

    @GetMapping("/horarios")
    public List<HorarioTrabalhoResponse> listarHorarios(@AuthenticationPrincipal UsuarioLogado logado) {
        return horarioTrabalhoService.listar(logado.barbeiroId());
    }

    @PutMapping("/horarios/{dia}")
    public HorarioTrabalhoResponse definirHorario(@AuthenticationPrincipal UsuarioLogado logado,
                                                  @PathVariable DiaSemana dia,
                                                  @Valid @RequestBody HorarioTrabalhoRequest request) {
        return horarioTrabalhoService.definir(logado.barbeiroId(), dia, request);
    }

    @DeleteMapping("/horarios/{dia}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerHorario(@AuthenticationPrincipal UsuarioLogado logado, @PathVariable DiaSemana dia) {
        horarioTrabalhoService.remover(logado.barbeiroId(), dia);
    }
}
