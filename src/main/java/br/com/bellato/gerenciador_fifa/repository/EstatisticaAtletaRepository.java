package br.com.bellato.gerenciador_fifa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;

@Repository
public interface EstatisticaAtletaRepository extends JpaRepository<EstatisticaAtleta, Long> {
}
