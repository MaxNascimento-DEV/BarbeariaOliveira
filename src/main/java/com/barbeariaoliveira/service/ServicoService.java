package com.barbeariaoliveira.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.barbeariaoliveira.dto.ServicoRequest;
import com.barbeariaoliveira.dto.ServicoResponse;
import com.barbeariaoliveira.entity.Servico;
import com.barbeariaoliveira.exception.RecursoNaoEncontradoException;
import com.barbeariaoliveira.exception.RegraNegocioException;
import com.barbeariaoliveira.mapper.ServicoMapper;
import com.barbeariaoliveira.repository.ServicoRepository;

import org.springframework.transaction.annotation.Transactional;


@Service
public class ServicoService {
    private final ServicoRepository servicoRepository;
    private final ServicoMapper servicoMapper;
    
    public ServicoService(ServicoRepository servicoRepository, ServicoMapper servicoMapper){
        this.servicoRepository = servicoRepository;
        this.servicoMapper = servicoMapper; 
    }

    @Transactional 
    public ServicoResponse criar(ServicoRequest request){
        if(servicoRepository.existsByNomeIgnoreCase(request.nome())){
            throw new RegraNegocioException("servico ja cadastrado."); 
        }

        Servico servico = servicoMapper.toEntity(request); 
        Servico salvo = servicoRepository.save(servico);
        return servicoMapper.toResponse(salvo); 
    }

    @Transactional(readOnly = true)
    public List<ServicoResponse> listarAtivos(){
        List<Servico> servicos = servicoRepository.findByAtivoTrueOrderByNomeDesc();
        List<ServicoResponse> respostas = new ArrayList<>();
        for(Servico servico : servicos){
            respostas.add(servicoMapper.toResponse(servico));
        }
        
        return respostas;
    }

    @Transactional
    public void desativar(Long id){
        Servico servico = buscarEntidadePorId(id); 
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }

    @Transactional 
    public void ativar(Long id){
        Servico servico = buscarEntidadePorId(id); 
        servico.setAtivo(true);
        servicoRepository.save(servico); 
    }
    
    private Servico buscarEntidadePorId(Long id){
        Servico servico = servicoRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Serviço não encontrado."));
        return servico; 
    }

    @Transactional
    public ServicoResponse atualizar(Long id, ServicoRequest request){
        Servico servico = buscarEntidadePorId(id);
        if(servicoRepository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)){
            throw new RegraNegocioException("Já existe um serviço com esse nome.");
        }

        servico.setNome(request.nome());
        servico.setValor(request.valor());
        servico.setDuracaoMinutos(request.duracaoMinutos());
        
        servicoRepository.save(servico);

        return servicoMapper.toResponse(servico); 
    } 
    
    @Transactional(readOnly = true)
    public List<ServicoResponse> listarTodos(){
        List<Servico> servicos = servicoRepository.findAllByOrderByNomeAsc();
        List<ServicoResponse> resposta = new ArrayList<>();
        for( Servico servico : servicos ){
            resposta.add(servicoMapper.toResponse(servico));
        }

        return  resposta; 
    } 

    @Transactional(readOnly = true)
    public ServicoResponse buscarPorId(Long id){
        return servicoMapper.toResponse(buscarEntidadePorId(id)); 
    }

}
