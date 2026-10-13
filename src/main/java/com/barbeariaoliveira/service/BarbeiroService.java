package com.barbeariaoliveira.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.barbeariaoliveira.dto.BarbeiroPublicResponse;
import com.barbeariaoliveira.dto.BarbeiroRequest;
import com.barbeariaoliveira.dto.BarbeiroResponse;
import com.barbeariaoliveira.entity.Barbeiro;
import com.barbeariaoliveira.exception.RecursoNaoEncontradoException;
import com.barbeariaoliveira.exception.RegraNegocioException;
import com.barbeariaoliveira.mapper.BarbeiroMapper;
import com.barbeariaoliveira.repository.BarbeiroRepository;

import org.springframework.transaction.annotation.Transactional;

@Service 
public class BarbeiroService {

    private final BarbeiroRepository barbeiroRepository; 
    private final BarbeiroMapper barbeiroMapper;

    public BarbeiroService(BarbeiroMapper barbeiroMapper, BarbeiroRepository barbeiroRepository){
        this.barbeiroMapper = barbeiroMapper;
        this.barbeiroRepository = barbeiroRepository;
    }

   private String normalizarTelefone(String telefone) {
    return telefone.replaceAll("\\D", "");
    }

    private String normalizaEspecialidade(String especialidade){
        if(especialidade == null || especialidade.isBlank()){
            return null; 
        }
        return especialidade.strip(); 
    }

    private Barbeiro buscarEntidadePorId(Long id){
        return barbeiroRepository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException("Barbeiro não encontrado"));
    }

    /* ------------ // ------------------  */

    @Transactional 
    public BarbeiroResponse criar(BarbeiroRequest request){
        String telefone = normalizarTelefone(request.telefone());
        if(barbeiroRepository.existsByTelefone(telefone)){
            throw new RegraNegocioException("Já existe um barbeiro com esse telefone."); 
        }

        Barbeiro barbeiro = barbeiroMapper.toEntity(request);
        barbeiro.setTelefone(telefone);
        barbeiro.setEspecialidade(normalizaEspecialidade(request.especialidade()));

        Barbeiro salvo = barbeiroRepository.save(barbeiro);
        return barbeiroMapper.toResponse(salvo); 
    }

    @Transactional(readOnly = true)
    public List<BarbeiroPublicResponse> listarAtivos(){
        List<Barbeiro> barbeiros = barbeiroRepository.findByAtivoTrueOrderByNomeAsc();
        List<BarbeiroPublicResponse> respostas = new ArrayList<>();
        for(Barbeiro barbeiro : barbeiros){
            respostas.add(barbeiroMapper.toPublicResponse(barbeiro)); 
        }
        return respostas; 
    } 

    @Transactional(readOnly = true)
    public List<BarbeiroResponse> listarTodos(){
        List<Barbeiro> barbeiros = barbeiroRepository.findAllByOrderByNomeAsc();
        List<BarbeiroResponse> respostas = new ArrayList<>();
        for(Barbeiro barbeiro : barbeiros){
            respostas.add(barbeiroMapper.toResponse(barbeiro));
        }

        return respostas; 
    }

    @Transactional(readOnly = true)
    public BarbeiroResponse buscarPorId(Long id){
        return barbeiroMapper.toResponse(buscarEntidadePorId(id)); 
    }

    @Transactional
    public BarbeiroResponse atualizar(Long id, BarbeiroRequest request){
        Barbeiro barbeiro = buscarEntidadePorId(id);
        String telefone = normalizarTelefone(request.telefone()); 

        if (barbeiroRepository.existsByTelefoneAndIdNot(telefone, id)) {
            throw new RegraNegocioException("ja existe um barbeiro com esse telefone.");             
        }

        barbeiro.setNome(request.nome());
        barbeiro.setEspecialidade(normalizaEspecialidade(request.especialidade()));
        barbeiro.setTelefone(request.telefone());

        barbeiroRepository.save(barbeiro);
        return barbeiroMapper.toResponse(barbeiro); 
    }

    @Transactional 
    public void ativar(Long id){
        Barbeiro barbeiro = buscarEntidadePorId(id);
        barbeiro.setAtivo(true);
        barbeiroRepository.save(barbeiro); 
    }

    @Transactional 
    public void desativar(Long id){
        Barbeiro barbeiro = buscarEntidadePorId(id);
        barbeiro.setAtivo(false);
        barbeiroRepository.save(barbeiro); 
    }
}
