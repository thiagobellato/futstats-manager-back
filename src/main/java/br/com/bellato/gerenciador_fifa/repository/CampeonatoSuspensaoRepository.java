package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoSuspensao;

@Repository
public interface CampeonatoSuspensaoRepository extends JpaRepository<CampeonatoSuspensao, Long> {

    List<CampeonatoSuspensao> findByCampeonatoCampeonatoIdAndAtivaTrue(Long campeonatoId);

    List<CampeonatoSuspensao> findByCampeonatoCampeonatoIdAndHerdadaTrue(Long campeonatoId);

    List<CampeonatoSuspensao> findByCampeonatoCampeonatoIdAndHerdadaTrueAndAtivaTrue(Long campeonatoId);

    void deleteByCampeonatoCampeonatoIdAndHerdadaFalse(Long campeonatoId);

    void deleteByCampeonatoCampeonatoId(Long campeonatoId);

    /**
     * Suspensões ainda ativas em campeonatos já finalizados (elegíveis à herança).
     */
    @Query("""
            SELECT s FROM CampeonatoSuspensao s
            JOIN FETCH s.campeonato c
            JOIN FETCH s.partidaOrigem
            WHERE s.ativa = true
              AND c.status = :status
              AND c.campeonatoId <> :excluirCampeonatoId
            """)
    List<CampeonatoSuspensao> findAtivasEmCampeonatosComStatus(
            @Param("status") StatusCampeonato status,
            @Param("excluirCampeonatoId") Long excluirCampeonatoId);
}
