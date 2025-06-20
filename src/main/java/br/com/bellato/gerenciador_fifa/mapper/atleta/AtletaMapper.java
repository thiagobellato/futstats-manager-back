package br.com.bellato.gerenciador_fifa.mapper.atleta;

import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseDTO;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;

public class AtletaMapper {

    public static Atleta toEntity(AtletaRequestDTO dto, Clube clube) {
        Atleta atleta = new Atleta();
        atleta.setNome(dto.getNome());
        atleta.setClube(clube);
        // atleta.setSobrenome(dto.getSobrenome());
        // atleta.setDataDeNascimento(dto.getDataDeNascimento());
        // atleta.setNacionalidade(dto.getNacionalidade());
        // atleta.setPosicao(dto.getPosicao());
        return atleta;
    }

    public static AtletaResponseDTO toDTO(Atleta atleta) {
        AtletaResponseDTO dto = new AtletaResponseDTO();
        dto.setAtletaId(atleta.getAtletaId());
        dto.setNome(atleta.getNome());
        dto.setPosicao(atleta.getPosicao());

        // dto.setClubeNome(atleta.getClube().getNome());

        if (atleta.getClube() != null) {
            dto.setClubeNome(atleta.getClube().getNome());
        } else {
            dto.setClubeNome("Sem Clube"); // Ou uma string padrão, tipo "Sem clube"
        }
        return dto;
    }
}
