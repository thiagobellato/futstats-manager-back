package br.com.bellato.gerenciador_fifa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.HistoricoClubeCampeonato;

@Repository
public interface HistoricoClubeCampeonatoRepository extends JpaRepository<HistoricoClubeCampeonato, Long> {

    boolean existsByCampeonatoCampeonatoId(Long campeonatoId);
}
