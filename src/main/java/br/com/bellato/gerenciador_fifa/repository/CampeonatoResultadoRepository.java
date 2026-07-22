package br.com.bellato.gerenciador_fifa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.CampeonatoResultado;

@Repository
public interface CampeonatoResultadoRepository extends JpaRepository<CampeonatoResultado, Long> {

    boolean existsByCampeonatoCampeonatoId(Long campeonatoId);

    Optional<CampeonatoResultado> findByCampeonatoCampeonatoId(Long campeonatoId);
}
