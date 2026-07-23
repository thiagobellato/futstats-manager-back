package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;

@Repository
public interface CampeonatoAtletaRepository extends JpaRepository<CampeonatoAtleta, Long> {

    @Query("""
            SELECT a FROM CampeonatoAtleta a
            LEFT JOIN FETCH a.campeonatoClube
            WHERE a.campeonato.campeonatoId = :campeonatoId
            """)
    List<CampeonatoAtleta> findByCampeonatoCampeonatoId(@Param("campeonatoId") Long campeonatoId);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeIdAndAtivoTrue(Long campeonatoClubeId);

    List<CampeonatoAtleta> findByCampeonatoClubeCampeonatoClubeIdInAndAtivoTrue(List<Long> campeonatoClubeIds);

    List<CampeonatoAtleta> findByCampeonatoCampeonatoIdAndAtletaOrigemId(Long campeonatoId, Long atletaOrigemId);

    Optional<CampeonatoAtleta> findByCampeonatoCampeonatoIdAndAtletaOrigemIdAndAtivoTrue(
            Long campeonatoId, Long atletaOrigemId);

    List<CampeonatoAtleta> findByCampeonatoCampeonatoIdAndIdentidade(Long campeonatoId, String identidade);
}
