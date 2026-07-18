package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoRodada;

@Repository
public interface CampeonatoRodadaRepository extends JpaRepository<CampeonatoRodada, Long> {

    List<CampeonatoRodada> findByCampeonatoCampeonatoIdOrderByNumeroRodadaAsc(Long campeonatoId);

    Optional<CampeonatoRodada> findByCampeonatoCampeonatoIdAndNumeroRodada(Long campeonatoId, Integer numeroRodada);
}
