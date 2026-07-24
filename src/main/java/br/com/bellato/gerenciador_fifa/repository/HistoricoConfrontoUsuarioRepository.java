package br.com.bellato.gerenciador_fifa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.HistoricoConfrontoUsuario;

@Repository
public interface HistoricoConfrontoUsuarioRepository extends JpaRepository<HistoricoConfrontoUsuario, Long> {

    Optional<HistoricoConfrontoUsuario> findByCampeonatoCampeonatoId(Long campeonatoId);

    boolean existsByCampeonatoCampeonatoId(Long campeonatoId);

    @Query("""
            SELECT h FROM HistoricoConfrontoUsuario h
            JOIN FETCH h.campeonato
            JOIN FETCH h.participanteA pa
            JOIN FETCH h.participanteB pb
            LEFT JOIN FETCH pa.user
            LEFT JOIN FETCH pb.user
            LEFT JOIN FETCH h.vencedor v
            LEFT JOIN FETCH v.user
            LEFT JOIN FETCH h.clubeCampeao
            WHERE (pa.user.userId = :userId1 AND pb.user.userId = :userId2)
               OR (pa.user.userId = :userId2 AND pb.user.userId = :userId1)
            """)
    List<HistoricoConfrontoUsuario> listarPorParUsuarios(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);
}
