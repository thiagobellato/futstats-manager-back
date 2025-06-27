package br.com.bellato.gerenciador_fifa.mapper.atleta;

import org.apache.commons.lang3.StringUtils;

import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;

public class AtletaMapper {

    public static Atleta toEntity(AtletaRequestDTO dto, Clube clube) {
        Atleta atleta = new Atleta();
        atleta.setNome(dto.getNome());
        atleta.setClube(clube);

        return atleta;
    }

    public static AtletaResponseDTO toDTO(Atleta atleta) {
        AtletaResponseDTO dto = new AtletaResponseDTO();
        dto.setAtletaId(atleta.getAtletaId());
        dto.setNome(atleta.getNome());

        if (atleta.getClube() != null && StringUtils.isNotBlank(atleta.getClube().getNome())) {
            dto.setClubeNome(atleta.getClube().getNome());
        } else {
            dto.setClubeNome("Sem Clube");
        }
        return dto;
    }

    public static AtletaResponseCompletoDTO toDTOCompleto(Atleta atleta) {
        AtletaResponseCompletoDTO dto = new AtletaResponseCompletoDTO();
        dto.setAtletaId(atleta.getAtletaId());
        dto.setNome(atleta.getNome());
        dto.setSobrenome(atleta.getSobrenome());
        dto.setPosicao(atleta.getPosicao());
        dto.setNacionalidade(atleta.getNacionalidade());

        if (atleta.getClube() != null && StringUtils.isNotBlank(atleta.getClube().getNome())) {
            dto.setClubeNome(atleta.getClube().getNome());
        } else {
            dto.setClubeNome("Sem Clube");
        }
        return dto;
    }
}
