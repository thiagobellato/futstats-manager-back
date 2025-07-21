package br.com.bellato.gerenciador_fifa.mapper.estatistica_atleta;

import java.time.LocalDate;

import br.com.bellato.gerenciador_fifa.dto.estatistica_atleta.EstatisticaAtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.estatistica_atleta.EstatisticaAtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.EstatisticaAtleta;

public class EstatisticaAtletaMapper {

    public static EstatisticaAtleta toEntity(EstatisticaAtletaRequestDTO dto, Clube clube, Atleta atleta) {
        EstatisticaAtleta estatisticaAtleta = new EstatisticaAtleta();
        estatisticaAtleta.setClube(clube);
        estatisticaAtleta.setAtleta(atleta);
        estatisticaAtleta.setGols(dto.getGols());
        estatisticaAtleta.setAssistencias(dto.getAssistencias());
        estatisticaAtleta.setDataInicio(LocalDate.now());

        return estatisticaAtleta;

    }

    public static EstatisticaAtletaResponseDTO toDTO(EstatisticaAtleta estatisticaAtleta) {
        EstatisticaAtletaResponseDTO dto = new EstatisticaAtletaResponseDTO();
        dto.setEstatisticaAtletaId(null);
        dto.setNomeAtleta(estatisticaAtleta.getAtleta().getNome());
        dto.setNomeClube(estatisticaAtleta.getClube().getNome());
        dto.setGols(estatisticaAtleta.getGols());
        dto.setAssistencias(estatisticaAtleta.getAssistencias());

        return dto;
    }

}
