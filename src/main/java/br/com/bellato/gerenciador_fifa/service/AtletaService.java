package br.com.bellato.gerenciador_fifa.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.mapper.atleta.AtletaMapper;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AtletaService {

    @Autowired
    private AtletaRepository atletaRepository;

    @Autowired
    private ClubeRepository clubeRepository; // ✅ Corrigido aqui

    public List<Atleta> obterTodos() {
        return atletaRepository.findAll(); // ✅ mais direto
    }

    public Atleta obterPorId(long id) {
        return atletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nenhum registro encontrado para o ID: " + id));
    }

    public AtletaResponseDTO adicionar(AtletaRequestDTO dto) {
        // ✅ Busca o clube pelo ID
        // Clube clube = clubeRepository.findById(dto.getClubeId())
        // .orElseThrow(() -> new RuntimeException("Clube não encontrado com ID: " +
        // dto.getClubeId()));

        Clube clube = null;
        if (dto.getClubeId() != null) {
            clube = clubeRepository.findById(dto.getClubeId())
                    .orElseThrow(() -> new RuntimeException("Clube não encontrado com ID: " + dto.getClubeId()));
        }

        // ✅ Usa o mapper passando o clube já instanciado
        Atleta atleta = AtletaMapper.toEntity(dto, clube);
        Atleta salvo = atletaRepository.save(atleta);
        return AtletaMapper.toDTO(salvo);
    }

    public List<Atleta> adicionarEmLote(List<Atleta> atletas) {
        return atletaRepository.saveAll(atletas);
    }

    public boolean apagarPorId(Long id) {
        if (atletaRepository.existsById(id)) {
            atletaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Atleta atualizarPorId(Long id, Atleta dadosAtualizados) {
        Atleta atletaExistente = atletaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atleta não encontrado com o ID: " + id));

        if (dadosAtualizados.getNome() != null) {
            atletaExistente.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getSobrenome() != null) {
            atletaExistente.setSobrenome(dadosAtualizados.getSobrenome());
        }
        if (dadosAtualizados.getDataDeNascimento() != null) {
            atletaExistente.setDataDeNascimento(dadosAtualizados.getDataDeNascimento());
        }
        if (dadosAtualizados.getNacionalidade() != null) {
            atletaExistente.setNacionalidade(dadosAtualizados.getNacionalidade());
        }
        if (dadosAtualizados.getPosicao() != null) {
            atletaExistente.setPosicao(dadosAtualizados.getPosicao());
        }

        return atletaRepository.save(atletaExistente);
    }
}
