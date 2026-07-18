package br.com.bellato.gerenciador_fifa.mapper.clube;

import br.com.bellato.gerenciador_fifa.dto.clube.ClubeRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseDTO;
import br.com.bellato.gerenciador_fifa.model.Clube;

public class ClubeMapper {

    public static Clube toEntity(ClubeRequestDTO dto) {
        Clube clube = new Clube();
        clube.setNome(dto.getNome());
        clube.setSigla(dto.getSigla());
        clube.setPais(dto.getPais());
        if (dto.getRank() != null) {
            clube.setRank(dto.getRank());
        }
        return clube;
    }

    public static ClubeResponseDTO toDTO(Clube clube) {
        ClubeResponseDTO dto = new ClubeResponseDTO();
        dto.setClubeId(clube.getClubeId());
        dto.setNome(clube.getNome());

        return dto;
    }

    public static ClubeResponseCompletoDTO toDTOCompleto(Clube clube) {
        ClubeResponseCompletoDTO dto = new ClubeResponseCompletoDTO();
        dto.setClubeId(clube.getClubeId());
        dto.setNome(clube.getNome());
        dto.setSigla(clube.getSigla());
        dto.setPais(clube.getPais());
        dto.setRank(clube.getRank());

        return dto;
    }
}
