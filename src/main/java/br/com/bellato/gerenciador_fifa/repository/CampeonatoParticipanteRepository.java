package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoParticipante;

@Repository
public interface CampeonatoParticipanteRepository extends JpaRepository<CampeonatoParticipante, Long> {

    List<CampeonatoParticipante> findByCampeonatoCampeonatoIdOrderBySideAsc(Long campeonatoId);

    @Query("""
            SELECT p FROM CampeonatoParticipante p
            LEFT JOIN FETCH p.user
            WHERE p.campeonato.campeonatoId = :campeonatoId
            ORDER BY p.side ASC
            """)
    List<CampeonatoParticipante> findByCampeonatoIdComUser(@Param("campeonatoId") Long campeonatoId);

    Optional<CampeonatoParticipante> findByCampeonatoCampeonatoIdAndSide(Long campeonatoId, Integer side);

    long countByCampeonatoCampeonatoId(Long campeonatoId);
}
