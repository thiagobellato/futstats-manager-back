package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoPartida;

@Repository
public interface CampeonatoPartidaRepository extends JpaRepository<CampeonatoPartida, Long> {

    Optional<CampeonatoPartida> findByCampeonatoPartidaIdAndCampeonatoRodadaCampeonatoCampeonatoId(
            Long partidaId, Long campeonatoId);
}
