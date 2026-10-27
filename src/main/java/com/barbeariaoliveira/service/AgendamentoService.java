package com.barbeariaoliveira.service;

import java.time.*;
import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barbeariaoliveira.config.BarbeariaProperties;
import com.barbeariaoliveira.dto.AgendamentoRequest;
import com.barbeariaoliveira.dto.AgendamentoResponse;
import com.barbeariaoliveira.entity.Agendamento;
import com.barbeariaoliveira.entity.Barbeiro;
import com.barbeariaoliveira.entity.HorarioTrabalho;
import com.barbeariaoliveira.entity.Servico;
import com.barbeariaoliveira.enums.DiaSemana;
import com.barbeariaoliveira.enums.StatusAgendamento;
import com.barbeariaoliveira.exception.RecursoNaoEncontradoException;
import com.barbeariaoliveira.exception.RegraNegocioException;
import com.barbeariaoliveira.mapper.AgendamentoMapper;
import com.barbeariaoliveira.repository.AgendamentoRepository;
import com.barbeariaoliveira.repository.BarbeiroRepository;
import com.barbeariaoliveira.repository.HorarioTrabalhoRepository;
import com.barbeariaoliveira.repository.ServicoRepository;
import com.barbeariaoliveira.util.TelefoneUtils;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final HorarioTrabalhoRepository horarioTrabalhoRepository;
    private final AgendamentoMapper agendamentoMapper;
    private final BarbeariaProperties barbeariaProperties;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
            ServicoRepository servicoRepository,
            BarbeiroRepository barbeiroRepository,
            HorarioTrabalhoRepository horarioTrabalhoRepository,
            AgendamentoMapper agendamentoMapper,
            BarbeariaProperties barbeariaProperties) {
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
        this.barbeiroRepository = barbeiroRepository;
        this.horarioTrabalhoRepository = horarioTrabalhoRepository;
        this.agendamentoMapper = agendamentoMapper;
        this.barbeariaProperties = barbeariaProperties;
    }

    // ---------------- Ações públicas (chamadas pelos Controllers) ----------------

    @Transactional
    public AgendamentoResponse criar(AgendamentoRequest request) {
        // Serviço e barbeiro ativos
        Servico servico = buscarServicoAtivo(request.servicoId());
        Barbeiro barbeiro = buscarBarbeiroAtivo(request.barbeiroId());

        // Início e fim em UTC
        ZoneId fuso = barbeariaProperties.fusoHorario();
        Instant inicio = request.dataHoraInicio().atZone(fuso).toInstant();
        Instant fim = inicio.plus(Duration.ofMinutes(servico.getDuracaoMinutos()));

        // a janela é verificada ANTES da folga, para dar a mensagem mais útil
        Instant agora = Instant.now();
        Optional<String> motivoJanela = verificarJanela(inicio, agora);
        if (motivoJanela.isPresent()) {
            throw new RegraNegocioException(motivoJanela.get());
        }

        // O barbeiro trabalha nesse dia?
        DiaSemana dia = DiaSemana.de(request.dataHoraInicio().getDayOfWeek());
        HorarioTrabalho horario = horarioTrabalhoRepository
                .findByBarbeiroIdAndDiaSemana(barbeiro.getId(), dia)
                .orElseThrow(() -> new RegraNegocioException("O barbeiro não atende neste dia."));

        // Buscar o que já ocupa o barbeiro nesse dia
        LocalDate data = request.dataHoraInicio().toLocalDate();
        List<Agendamento> ocupados = agendamentoRepository.buscarOcupadosNoPeriodo(
                barbeiro.getId(),
                StatusAgendamento.ATIVOS,
                data.atStartOfDay(fuso).toInstant(),
                data.plusDays(1).atStartOfDay(fuso).toInstant());

        // Expediente, almoço e conflito
        Optional<String> motivo = verificarHorario(
                request.dataHoraInicio(), servico, horario, ocupados, agora); // ⭐ "agora" no lugar de Instant.now()
        if (motivo.isPresent()) {
            throw new RegraNegocioException(motivo.get());
        }

        // O cliente já tem agendamento ativo?
        String telefone = TelefoneUtils.normalizar(request.telefoneCliente());
        if (agendamentoRepository.existsByTelefoneClienteAndStatusIn(telefone, StatusAgendamento.ATIVOS)) {
            throw new RegraNegocioException("Você já possui um agendamento ativo.");
        }

        // Montar, salvar e devolver
        Agendamento agendamento = Agendamento.builder()
                .barbeiro(barbeiro)
                .servico(servico)
                .nomeCliente(request.nomeCliente().strip())
                .telefoneCliente(telefone)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .valorCobrado(servico.getValor())
                .build();

        Agendamento salvo = agendamentoRepository.save(agendamento);
        return agendamentoMapper.toResponse(salvo);
    }

    @Transactional
    public AgendamentoResponse confirmar(Long id) {
        Agendamento agendamento = buscarEntidadePorId(id);
        mudarStatus(agendamento, StatusAgendamento.CONFIRMADO);
        Agendamento salvo = agendamentoRepository.save(agendamento);
        return agendamentoMapper.toResponse(salvo);
    }

    @Transactional
    public AgendamentoResponse cancelar(Long id) {
        Agendamento agendamento = buscarEntidadePorId(id);
        mudarStatus(agendamento, StatusAgendamento.CANCELADO);
        Agendamento salvo = agendamentoRepository.save(agendamento);
        return agendamentoMapper.toResponse(salvo);
    }

    @Transactional(readOnly = true)
    public List<AgendamentoResponse> listarPorData(LocalDate data) {
        ZoneId fuso = barbeariaProperties.fusoHorario();
        Instant inicioDoDia = data.atStartOfDay(fuso).toInstant();
        Instant fimDoDia = data.plusDays(1).atStartOfDay(fuso).toInstant();

        List<Agendamento> agendamentos = agendamentoRepository.buscarPorPeriodo(inicioDoDia, fimDoDia);

        List<AgendamentoResponse> respostas = new ArrayList<>();
        for (Agendamento agendamento : agendamentos) {
            respostas.add(agendamentoMapper.toResponse(agendamento));
        }
        return respostas;
    }

    @Transactional
    public void processarTransicoesAutomaticas() {
        Instant agora = Instant.now();

        // 1. Cancelar os não confirmados dentro do prazo
        List<Agendamento> naoConfirmados = agendamentoRepository.findByStatusAndDataHoraInicioLessThanEqual(
                StatusAgendamento.AGENDADO, agora.plus(barbeariaProperties.prazoConfirmacao()));
        for (Agendamento agendamento : naoConfirmados) {
            mudarStatus(agendamento, StatusAgendamento.CANCELADO);
            // TODO etapa 8: avisar o cliente pelo WhatsApp
        }

        // 2. Iniciar os confirmados cujo horário chegou
        List<Agendamento> paraIniciar = agendamentoRepository.findByStatusAndDataHoraInicioLessThanEqual(
                StatusAgendamento.CONFIRMADO, agora);
        for (Agendamento agendamento : paraIniciar) {
            mudarStatus(agendamento, StatusAgendamento.EM_ANDAMENTO);
        }

        // 3. Concluir os em andamento cujo fim chegou
        List<Agendamento> paraConcluir = agendamentoRepository.findByStatusAndDataHoraFimLessThanEqual(
                StatusAgendamento.EM_ANDAMENTO, agora);
        for (Agendamento agendamento : paraConcluir) {
            mudarStatus(agendamento, StatusAgendamento.CONCLUIDO);
        }
    }

    @Transactional(readOnly = true)
    public List<LocalTime> listarDisponiveis(Long barbeiroId, Long servicoId, LocalDate data) {
        Servico servico = buscarServicoAtivo(servicoId);
        Barbeiro barbeiro = buscarBarbeiroAtivoSemLock(barbeiroId); // sem a chave!

        // O barbeiro trabalha nesse dia? (decisão 1: erro 409)
        DiaSemana dia = DiaSemana.de(data.getDayOfWeek());
        HorarioTrabalho horario = horarioTrabalhoRepository
                .findByBarbeiroIdAndDiaSemana(barbeiro.getId(), dia)
                .orElseThrow(() -> new RegraNegocioException("O barbeiro não atende neste dia."));

        // O que já ocupa o barbeiro nesse dia (uma consulta só)
        ZoneId fuso = barbeariaProperties.fusoHorario();
        List<Agendamento> ocupados = agendamentoRepository.buscarOcupadosNoPeriodo(
                barbeiro.getId(),
                StatusAgendamento.ATIVOS,
                data.atStartOfDay(fuso).toInstant(),
                data.plusDays(1).atStartOfDay(fuso).toInstant());

        // Testar cada candidato, de intervalo em intervalo
        Instant agora = Instant.now();
        LocalDateTime fechamento = data.atTime(horario.getHoraFechamento());
        LocalDateTime candidato = data.atTime(horario.getHoraAbertura());
        List<LocalTime> disponiveis = new ArrayList<>();

        while (!candidato.plusMinutes(servico.getDuracaoMinutos()).isAfter(fechamento)) {
            if (verificarHorario(candidato, servico, horario, ocupados, agora).isEmpty()) {
                disponiveis.add(candidato.toLocalTime());
            }
            candidato = candidato.plus(barbeariaProperties.intervaloHorarios());
        }

        return disponiveis;
    }

    // ---------------- Auxiliares (private) ----------------

    private Servico buscarServicoAtivo(Long servicoId) {
        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado."));
        if (!servico.getAtivo()) {
            throw new RegraNegocioException("Este serviço não está disponível para agendamento.");
        }
        return servico;
    }

    // Para o criar: busca COM a chave
    private Barbeiro buscarBarbeiroAtivo(Long barbeiroId) {
        Barbeiro barbeiro = barbeiroRepository.buscarComLock(barbeiroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Barbeiro não encontrado."));
        validarBarbeiroAtivo(barbeiro);
        return barbeiro;
    }

    // Para consultas: busca SEM a chave
    private Barbeiro buscarBarbeiroAtivoSemLock(Long barbeiroId) {
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Barbeiro não encontrado."));
        validarBarbeiroAtivo(barbeiro);
        return barbeiro;
    }

    // A regra do "ativo", num lugar só
    private void validarBarbeiroAtivo(Barbeiro barbeiro) {
        if (!barbeiro.getAtivo()) {
            throw new RegraNegocioException("Este barbeiro não está disponível para agendamento.");
        }
    }

    private Agendamento buscarEntidadePorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));
    }

    private void mudarStatus(Agendamento agendamento, StatusAgendamento novo) {
        StatusAgendamento atual = agendamento.getStatus();
        if (!atual.podeMudarPara(novo)) {
            throw new RegraNegocioException(
                    "Não é possível alterar o agendamento de " + atual + " para " + novo + ".");
        }
        agendamento.setStatus(novo);
    }

    // a regra da janela de antecedência, num lugar só
    private Optional<String> verificarJanela(Instant inicio, Instant agora) {
        if (inicio.isBefore(agora.plus(barbeariaProperties.antecedenciaMinima()))) {
            return Optional.of("Agendamentos precisam de pelo menos "
                    + barbeariaProperties.antecedenciaMinima().toMinutes() + " minutos de antecedência.");
        }
        if (inicio.isAfter(agora.plus(Duration.ofDays(barbeariaProperties.diasMaximosAntecedencia())))) {
            return Optional.of("Agendamentos podem ser feitos com até "
                    + barbeariaProperties.diasMaximosAntecedencia() + " dias de antecedência.");
        }
        return Optional.empty();
    }

    private Optional<String> verificarHorario(LocalDateTime inicioLocal,
            Servico servico,
            HorarioTrabalho horario,
            List<Agendamento> ocupados,
            Instant agora) {
        ZoneId fuso = barbeariaProperties.fusoHorario();
        LocalDateTime fimLocal = inicioLocal.plusMinutes(servico.getDuracaoMinutos());
        Instant inicio = inicioLocal.atZone(fuso).toInstant();
        Instant fim = inicio.plus(Duration.ofMinutes(servico.getDuracaoMinutos()));

        // Antecedência mínima e limite
        Optional<String> motivoJanela = verificarJanela(inicio, agora);
        if (motivoJanela.isPresent()) {
            return motivoJanela;
        }

        // Dentro do expediente
        LocalDate data = inicioLocal.toLocalDate();
        LocalDateTime abertura = data.atTime(horario.getHoraAbertura());
        LocalDateTime fechamento = data.atTime(horario.getHoraFechamento());
        if (inicioLocal.isBefore(abertura) || fimLocal.isAfter(fechamento)) {
            return Optional.of("O barbeiro atende das " + horario.getHoraAbertura()
                    + " às " + horario.getHoraFechamento() + " neste dia.");
        }

        // alinhado à grade de horários (ex.: 09:00, 09:30, 10:00...)
        long minutosDesdeAbertura = Duration.between(abertura, inicioLocal).toMinutes();
        long intervalo = barbeariaProperties.intervaloHorarios().toMinutes();
        boolean alinhado = inicioLocal.getSecond() == 0
                && inicioLocal.getNano() == 0
                && minutosDesdeAbertura % intervalo == 0;
        if (!alinhado) {
            return Optional.of("Escolha um dos horários disponíveis (de "
                    + intervalo + " em " + intervalo + " minutos).");
        }

        // Fora do almoço
        if (horario.getAlmocoInicio() != null) {
            LocalDateTime almocoInicio = data.atTime(horario.getAlmocoInicio());
            LocalDateTime almocoFim = data.atTime(horario.getAlmocoFim());
            if (inicioLocal.isBefore(almocoFim) && fimLocal.isAfter(almocoInicio)) {
                return Optional.of("Este horário coincide com o intervalo de almoço do barbeiro ("
                        + horario.getAlmocoInicio() + " às " + horario.getAlmocoFim() + ").");
            }
        }

        // Conflito com outros agendamentos
        for (Agendamento ocupado : ocupados) {
            if (ocupado.getDataHoraInicio().isBefore(fim) && ocupado.getDataHoraFim().isAfter(inicio)) {
                return Optional.of("Este horário já está ocupado. Escolha outro horário.");
            }
        }

        return Optional.empty();
    }
}