package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;

@Repository
public interface CampeonatoAtletaRepository extends JpaRepository<CampeonatoAtleta, Long> {

    List<CampeonatoAtleta> findByCampeonatoCampeonatoId(Long campeonatoId);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeId(Long campeonatoClubeId);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeIdIn(List<Long> campeonatoClubeIds);

    long countByCampeonatoCampeonatoId(Long campeonatoId);
}
