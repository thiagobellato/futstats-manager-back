package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.repository.ClubeRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ClubeService {
    @Autowired
    private ClubeRepository ClubeRepository;

    public List<Clube> obterTodos() {

        List<Clube> tipos = ClubeRepository.findAll();

        return tipos
                .stream()
                .collect(Collectors.toList());
    }

    public Clube obterPorId(long id) {

        Optional<Clube> optTipo = ClubeRepository.findById(id);

        if (optTipo.isEmpty()) {
            throw new RuntimeException("Nenhum registro encontrado para o ID: " + id);
        }

        return optTipo.get();
    }

    public Clube adicionar(Clube Clube) {

        // Clube.setClubeId((long) 0);

        return Clube = ClubeRepository.save(Clube);
    }

     public List<Clube> adicionarEmLote(List<Clube> clubes) {
    return ClubeRepository.saveAll(clubes);
}

    public boolean apagarPorId(Long id) {
        Optional<Clube> ClubeOptional = ClubeRepository.findById(id);
        if (ClubeOptional.isPresent()) {
            ClubeRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Clube atualizarPorId(Long id, Clube dadosAtualizados) {
        Clube ClubeExistente = ClubeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Clube não encontrado com o ID: " + id));

        // Atualiza apenas os campos não nulos de dadosAtualizados
        if (dadosAtualizados.getNome() != null) {
            ClubeExistente.setNome(dadosAtualizados.getNome());
        }
        if (dadosAtualizados.getPais() != null) {
            ClubeExistente.setPais(dadosAtualizados.getPais());
        }

        if (dadosAtualizados.getSigla() != null) {
            ClubeExistente.setSigla(dadosAtualizados.getSigla());
        }
        // Salva o Clube atualizado no banco de dados
        return ClubeRepository.save(ClubeExistente);
    }

}
