package br.com.bellato.gerenciador_fifa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;

public interface EstatisticaAtletaRepository extends JpaRepository<EstatisticaAtleta, Long> {
}
