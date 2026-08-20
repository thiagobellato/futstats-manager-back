package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.TaticaUsuarioClube;

@Repository
public interface TaticaUsuarioClubeRepository extends JpaRepository<TaticaUsuarioClube, Long> {

    @Query("SELECT t FROM TaticaUsuarioClube t "
            + "LEFT JOIN FETCH t.clube "
            + "LEFT JOIN FETCH t.jogadores j "
            + "LEFT JOIN FETCH j.atleta "
            + "WHERE t.usuario.userId = :userId AND t.clube.clubeId = :clubeId")
    Optional<TaticaUsuarioClube> findByUsuarioAndClube(@Param("userId") Long userId, @Param("clubeId") Long clubeId);

    @Query("SELECT t FROM TaticaUsuarioClube t "
            + "LEFT JOIN FETCH t.clube "
            + "LEFT JOIN FETCH t.jogadores j "
            + "LEFT JOIN FETCH j.atleta "
            + "WHERE t.taticaUsuarioClubeId = :id")
    Optional<TaticaUsuarioClube> findByIdComDetalhes(@Param("id") Long id);
}
