package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.HistoricoClubeCampeonato;

@Repository
public interface HistoricoClubeCampeonatoRepository extends JpaRepository<HistoricoClubeCampeonato, Long> {

    boolean existsByCampeonatoCampeonatoId(Long campeonatoId);

    List<HistoricoClubeCampeonato> findByCampeonatoCampeonatoId(Long campeonatoId);

    @Query("""
            SELECT h FROM HistoricoClubeCampeonato h
            JOIN FETCH h.campeonato c
            JOIN FETCH h.clube
            WHERE h.clube.clubeId = :clubeId
            ORDER BY c.dataFinalizacao DESC, c.dataCriacao DESC
            """)
    List<HistoricoClubeCampeonato> findByClubeIdOrderByCampeonatoDesc(@Param("clubeId") Long clubeId);

    @Query("""
            SELECT h FROM HistoricoClubeCampeonato h
            JOIN FETCH h.campeonato
            JOIN FETCH h.clube
            """)
    List<HistoricoClubeCampeonato> findAllComDetalhes();

    @Query("""
            SELECT h.clube.clubeId, h.clube.nome, COUNT(h)
            FROM HistoricoClubeCampeonato h
            WHERE (:busca IS NULL OR :busca = ''
                   OR LOWER(h.clube.nome) LIKE LOWER(CONCAT('%', :busca, '%')))
            GROUP BY h.clube.clubeId, h.clube.nome
            ORDER BY COUNT(h) DESC, h.clube.nome ASC
            """)
    Page<Object[]> rankingParticipacoes(@Param("busca") String busca, Pageable pageable);
}
