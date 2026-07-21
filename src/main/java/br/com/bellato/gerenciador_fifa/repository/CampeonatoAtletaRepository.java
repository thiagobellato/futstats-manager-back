package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;

@Repository
public interface CampeonatoAtletaRepository extends JpaRepository<CampeonatoAtleta, Long> {

    List<CampeonatoAtleta> findByCampeonatoCampeonatoId(Long campeonatoId);

    List<CampeonatoAtleta> findByCampeonatoCampeonatoIdAndAtivoTrue(Long campeonatoId);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeId(Long campeonatoClubeId);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeIdAndAtivoTrue(Long campeonatoClubeId);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeIdIn(List<Long> campeonatoClubeIds);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeIdInAndAtivoTrue(List<Long> campeonatoClubeIds);

    List<CampeonatoAtleta> findByCampeonatoCampeonatoIdAndAtletaOrigemId(Long campeonatoId, Long atletaOrigemId);

    Optional<CampeonatoAtleta> findByCampeonatoCampeonatoIdAndAtletaOrigemIdAndAtivoTrue(
            Long campeonatoId, Long atletaOrigemId);

    List<CampeonatoAtleta> findByCampeonatoCampeonatoIdAndIdentidade(Long campeonatoId, String identidade);

    long countByCampeonatoCampeonatoId(Long campeonatoId);

    long countByCampeonatoCampeonatoIdAndAtivoTrue(Long campeonatoId);
}
