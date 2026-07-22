package br.com.bellato.gerenciador_fifa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.HistoricoAtletaCampeonato;

@Repository
public interface HistoricoAtletaCampeonatoRepository extends JpaRepository<HistoricoAtletaCampeonato, Long> {

    boolean existsByCampeonatoCampeonatoId(Long campeonatoId);
}
