package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoResultado;

@Repository
public interface CampeonatoResultadoRepository extends JpaRepository<CampeonatoResultado, Long> {

    boolean existsByCampeonatoCampeonatoId(Long campeonatoId);

    @Query("""
            SELECT r FROM CampeonatoResultado r
            JOIN FETCH r.campeonato c
            JOIN FETCH r.campeaoClube
            LEFT JOIN FETCH r.viceCampeaoClube
            ORDER BY r.dataConquista DESC
            """)
    List<CampeonatoResultado> findAllComDetalhesOrderByDataDesc();

    @Query("""
            SELECT r FROM CampeonatoResultado r
            JOIN FETCH r.campeonato c
            JOIN FETCH r.campeaoClube
            LEFT JOIN FETCH r.viceCampeaoClube
            WHERE c.campeonatoId = :campeonatoId
            """)
    Optional<CampeonatoResultado> findDetalhadoByCampeonatoId(@Param("campeonatoId") Long campeonatoId);

    @Query("""
            SELECT r.viceCampeaoClube.clubeId, r.viceCampeaoClube.nome, COUNT(r)
            FROM CampeonatoResultado r
            WHERE r.viceCampeaoClube IS NOT NULL
              AND (:busca IS NULL OR :busca = ''
                   OR LOWER(r.viceCampeaoClube.nome) LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY r.viceCampeaoClube.clubeId, r.viceCampeaoClube.nome
            ORDER BY COUNT(r) DESC, r.viceCampeaoClube.nome ASC
            """)
    Page<Object[]> rankingVices(@Param("busca") String busca, Pageable pageable);
}
