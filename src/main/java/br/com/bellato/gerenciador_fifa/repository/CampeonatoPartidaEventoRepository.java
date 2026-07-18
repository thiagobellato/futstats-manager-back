package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoPartidaEvento;

@Repository
public interface CampeonatoPartidaEventoRepository extends JpaRepository<CampeonatoPartidaEvento, Long> {

    List<CampeonatoPartidaEvento> findByPartidaCampeonatoPartidaIdOrderByOrdemAsc(Long partidaId);

    List<CampeonatoPartidaEvento> findByPartidaCampeonatoRodadaCampeonatoCampeonatoId(Long campeonatoId);

    void deleteByPartidaCampeonatoPartidaId(Long partidaId);
}
