package br.com.bellato.gerenciador_fifa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.EstatisticaAtletaRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class EstatisticaAtletaService {

    @Autowired
    private EstatisticaAtletaRepository estatisticaRepository;
    private AtletaRepository atletaRepository;

    public List<EstatisticaAtleta> obterTodas() {
        return estatisticaRepository.findAll();
    }

    public EstatisticaAtleta obterPorId(Long atletaId) {
        return estatisticaRepository.findById(atletaId)
                .orElseThrow(() -> new EntityNotFoundException("Estatística não encontrada com o ID: " + atletaId));
    }

    public EstatisticaAtleta adicionar(EstatisticaAtleta estatistica) {
        return estatisticaRepository.save(estatistica);
    }

    /**
     * @param id
     * @param atualizada
     * @return
     */
    public EstatisticaAtleta atualizarParcialmente(Long id, EstatisticaAtleta atualizada) {
        EstatisticaAtleta existente = estatisticaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Estatística não encontrada com o ID: " + id));

        // Atualização condicional com verificação de nulos
        // if (atualizada.getAtleta() != null) {
        //     existente.setAtleta(atualizada.getAtleta());
        // }
        if (atualizada.getClube() != null) {
            existente.setClube(atualizada.getClube());
        }
        if (atualizada.getGols() != null) {
            existente.setGols(atualizada.getGols());
        }
        if (atualizada.getAssistencias() != null) {
            existente.setAssistencias(atualizada.getAssistencias());
        }
        if (atualizada.getDataInicio() != null) {
            existente.setDataInicio(atualizada.getDataInicio());
        }
        if (atualizada.getDataFim() != null) {
            existente.setDataFim(atualizada.getDataFim());
        }

        return estatisticaRepository.save(existente);
    }

    public boolean apagarPorId(Long id) {
        Optional<EstatisticaAtleta> estatisticaOptional = estatisticaRepository.findById(id);
        if (estatisticaOptional.isPresent()) {
            estatisticaRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
