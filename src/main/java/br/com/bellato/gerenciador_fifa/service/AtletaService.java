package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class AtletaService {
    @Autowired
    private AtletaRepository atletaRepository;

    public List<Atleta> obterTodos() {

        List<Atleta> tipos = atletaRepository.findAll();

        return tipos
                .stream()
                .collect(Collectors.toList());
    }

    public Atleta obterPorId(long id) {

        Optional<Atleta> optTipo = atletaRepository.findById(id);

        if (optTipo.isEmpty()) {
            throw new RuntimeException("Nenhum registro encontrado para o ID: " + id);
        }

        return optTipo.get();
    }

    public Atleta adicionar(Atleta atleta) {

        // atleta.setAtletaId((long) 0);

        return atleta = atletaRepository.save(atleta);
    }

    public List<Atleta> adicionarEmLote(List<Atleta> atletas) {
    return atletaRepository.saveAll(atletas);
}


    public boolean apagarPorId(Long id) {
        Optional<Atleta> atletaOptional = atletaRepository.findById(id);
        if (atletaOptional.isPresent()) {
            atletaRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Atleta atualizarPorId(Long id, Atleta dadosAtualizados) {
        Atleta atletaExistente = atletaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Atleta não encontrado com o ID: " + id));
    
        // Atualiza apenas os campos não nulos de dadosAtualizados
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
        // Exemplo de campos primitivos que podem ser atualizados apenas se diferentes do valor padrão
        if (dadosAtualizados.getGol() != 0) {
            atletaExistente.setGol(dadosAtualizados.getGol());
        }
        if (dadosAtualizados.getAssistencia() != 0) {
            atletaExistente.setAssistencia(dadosAtualizados.getAssistencia());
        }
    
        // Salva o atleta atualizado no banco de dados
        return atletaRepository.save(atletaExistente);
    }
    
}
