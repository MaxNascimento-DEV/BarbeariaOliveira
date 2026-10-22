package com.barbeariaoliveira.service;

import java.time.*;

import org.springframework.stereotype.Service;

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

import jakarta.transaction.Transactional;

@Service
public class AgendamentoService {
    
    private final AgendamentoRepository agendamentoRepository;
    private final ServicoRepository servicoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final HorarioTrabalhoRepository horarioTrabalhoRepository;
    private final AgendamentoMapper agendamentoMapper;
    private final BarbeariaProperties barbeariaProperties;

    
    public AgendamentoService(AgendamentoRepository agendamentoRepository, ServicoRepository servicoRepository, BarbeiroRepository barbeiroRepository, HorarioTrabalhoRepository horarioTrabalhoRepository, AgendamentoMapper agendamentoMapper, BarbeariaProperties barbeariaProperties){
        this.agendamentoRepository = agendamentoRepository;
        this.servicoRepository = servicoRepository;
        this.barbeiroRepository = barbeiroRepository;
        this.horarioTrabalhoRepository = horarioTrabalhoRepository;
        this.agendamentoMapper = agendamentoMapper;
        this.barbeariaProperties = barbeariaProperties;
    }

    private Servico buscarServicoAtivo(Long servicoId){
          Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado."));

        if (!servico.getAtivo()) {
            throw new RegraNegocioException("Este serviço não está disponivel para agendamento.");
        }

        return servico; 
    }

    private Barbeiro buscarBarbeiroAtivo(Long barbeiroId){
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId).orElseThrow(() -> new RecursoNaoEncontradoException("Barbeiro não encontrado"));

        if (!barbeiro.getAtivo()) {
            throw new RegraNegocioException("Este barbeiro não está disponivel para agendamento. ");
        }

        return barbeiro; 
    }

    @Transactional 
    public AgendamentoResponse criar(AgendamentoRequest request){
        Servico servico = buscarServicoAtivo(request.servicoId());
        Barbeiro barbeiro = buscarBarbeiroAtivo(request.barbeiroId()); 

        ZoneId fuso = barbeariaProperties.fusoHorario();
        Instant inicio = request.dataHoraInicio().atZone(fuso).toInstant();
        Instant fim = inicio.plus(Duration.ofMinutes(servico.getDuracaoMinutos()));
        
        Instant agora = Instant.now();
        Instant minimo = agora.plus(barbeariaProperties.antecedenciaMinima());
        Instant maximo = agora.plus(Duration.ofDays(barbeariaProperties.diasMaximosAntecedencia()));
        if (inicio.isBefore(minimo)) {
            throw new RegraNegocioException("Agendamentos precisam de pelo menos "
        + barbeariaProperties.antecedenciaMinima().toMinutes()
        + " minutos de antecedência.");
        }
        if (inicio.isAfter(maximo)) {
             throw new RegraNegocioException("Agendamentos podem ser feito com até "
        + barbeariaProperties.diasMaximosAntecedencia()
        + " dias de antecedência.");
        }

        DiaSemana dia = DiaSemana.de(request.dataHoraInicio().getDayOfWeek()); 
        HorarioTrabalho horario = horarioTrabalhoRepository.findByBarbeiroIdAndDiaSemana(barbeiro.getId(), dia).orElseThrow(() -> new RegraNegocioException("O barbeiro não atende neste dia."));

        LocalDateTime inicioLocal = request.dataHoraInicio();
        LocalDateTime fimLocal = inicioLocal.plusMinutes(servico.getDuracaoMinutos());

        LocalDate data = inicioLocal.toLocalDate();
        LocalDateTime abertura = data.atTime(horario.getHoraAbertura());
        LocalDateTime fechamento = data.atTime(horario.getHoraFechamento());

        if(inicioLocal.isBefore(abertura) || fimLocal.isAfter(fechamento)){
            throw new RegraNegocioException("O barbeiro atende das " + horario.getHoraAbertura()
            + " às " + horario.getHoraFechamento() + " neste dia.");
        }

        if(horario.getAlmocoInicio() != null){
            LocalDateTime almocoInicio = data.atTime(horario.getAlmocoInicio());
            LocalDateTime almocoFim = data.atTime(horario.getAlmocoFim()); 

            boolean invadeAlmoco = inicioLocal.isBefore(almocoFim) && fimLocal.isAfter(almocoInicio); 

            if(invadeAlmoco) {
                throw new RegraNegocioException("Este horário coincide com o intervalo de almoço do barbeiro ("
                + horario.getAlmocoInicio() + " às " + horario.getAlmocoFim() + ").");
            }
        }

        String telefone = TelefoneUtils.normalizar(request.telefoneCliente()); 
        if (agendamentoRepository.existsByTelefoneClienteAndStatusIn(telefone, StatusAgendamento.ATIVOS)) {
            throw new RegraNegocioException("Você já possui um agendamento ativo.");
        }
        
        if (agendamentoRepository.existeConflito(barbeiro.getId(), StatusAgendamento.ATIVOS, inicio, fim)) {
            throw new RegraNegocioException("Este horário já está ocupado. Escolha outro horário.");
        }

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





}
