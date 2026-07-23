package br.com.bellato.gerenciador_fifa.mapper.clube;

import br.com.bellato.gerenciador_fifa.dto.clube.ClubeRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.clube.ClubeResponseDTO;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.EstatisticaClube;

public class ClubeMapper {

    public static Clube toEntity(ClubeRequestDTO dto) {
        Clube clube = new Clube();
        clube.setNome(dto.getNome());
        clube.setSigla(dto.getSigla());
        clube.setPais(dto.getPais());
        EstatisticaClube estatistica = EstatisticaClube.zerada(clube);
        if (dto.getRank() != null) {
            estatistica.setRank(dto.getRank());
        }
        clube.setEstatistica(estatistica);
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

        EstatisticaClube estatistica = clube.getEstatistica();
        dto.setRank(estatistica != null ? estatistica.getRank() : null);
        dto.setGolsPro(valor(estatistica != null ? estatistica.getGolsPro() : null));
        dto.setGolsContra(valor(estatistica != null ? estatistica.getGolsContra() : null));
        dto.setVitorias(valor(estatistica != null ? estatistica.getVitorias() : null));
        dto.setEmpates(valor(estatistica != null ? estatistica.getEmpates() : null));
        dto.setDerrotas(valor(estatistica != null ? estatistica.getDerrotas() : null));
        dto.setTitulos(valor(estatistica != null ? estatistica.getTitulos() : null));

        return dto;
    }

    private static Integer valor(Integer value) {
        return value == null ? 0 : value;
    }
}
