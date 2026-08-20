package br.com.bellato.gerenciador_fifa.mapper.tatica;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import br.com.bellato.gerenciador_fifa.dto.atleta.AtletaResponseCompletoDTO;
import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaCompletoResponseDTO;
import br.com.bellato.gerenciador_fifa.dto.tatica.TaticaJogadorResponseDTO;
import br.com.bellato.gerenciador_fifa.enums.TipoTaticaJogador;
import br.com.bellato.gerenciador_fifa.mapper.atleta.AtletaMapper;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Clube;
import br.com.bellato.gerenciador_fifa.model.TaticaJogador;
import br.com.bellato.gerenciador_fifa.model.TaticaUsuarioClube;

public final class TaticaMapper {

    private TaticaMapper() {
    }

    public static TaticaCompletoResponseDTO toCompletoDTO(TaticaUsuarioClube tatica, List<Atleta> elenco) {
        TaticaCompletoResponseDTO dto = new TaticaCompletoResponseDTO();
        dto.setTaticaUsuarioClubeId(tatica.getTaticaUsuarioClubeId());

        Clube clube = tatica.getClube();
        if (clube != null) {
            dto.setClubeId(clube.getClubeId());
            dto.setClubeNome(clube.getNome());
            dto.setClubeSigla(clube.getSigla());
        }

        dto.setFormacao(tatica.getFormacao());
        dto.setAnotacoes(tatica.getAnotacoes());
        dto.setCapitaoAtletaId(tatica.getCapitaoAtletaId());
        dto.setBatedorPenaltisAtletaId(tatica.getBatedorPenaltisAtletaId());
        dto.setBatedorFaltaAtletaId(tatica.getBatedorFaltaAtletaId());
        dto.setBatedorEscanteioEsquerdoAtletaId(tatica.getBatedorEscanteioEsquerdoAtletaId());
        dto.setBatedorEscanteioDireitoAtletaId(tatica.getBatedorEscanteioDireitoAtletaId());
        dto.setDataUltimaAtualizacao(tatica.getDataUltimaAtualizacao());

        List<TaticaJogadorResponseDTO> titulares = tatica.getJogadores().stream()
                .filter(j -> j.getTipo() == TipoTaticaJogador.TITULAR)
                .map(TaticaMapper::toJogadorDTO)
                .collect(Collectors.toList());
        dto.setTitulares(titulares);

        List<TaticaJogadorResponseDTO> reservas = tatica.getJogadores().stream()
                .filter(j -> j.getTipo() == TipoTaticaJogador.RESERVA)
                .sorted(Comparator.comparing(
                        j -> j.getOrdemReserva() != null ? j.getOrdemReserva() : Integer.MAX_VALUE))
                .map(TaticaMapper::toJogadorDTO)
                .collect(Collectors.toList());
        dto.setReservas(reservas);

        List<AtletaResponseCompletoDTO> elencoDto = elenco.stream()
                .map(AtletaMapper::toDTOCompleto)
                .collect(Collectors.toList());
        dto.setElenco(elencoDto);

        return dto;
    }

    public static TaticaCompletoResponseDTO toVazioDTO(Clube clube, List<Atleta> elenco) {
        TaticaCompletoResponseDTO dto = new TaticaCompletoResponseDTO();
        dto.setClubeId(clube.getClubeId());
        dto.setClubeNome(clube.getNome());
        dto.setClubeSigla(clube.getSigla());
        dto.setElenco(elenco.stream().map(AtletaMapper::toDTOCompleto).collect(Collectors.toList()));
        return dto;
    }

    public static TaticaJogadorResponseDTO toJogadorDTO(TaticaJogador jogador) {
        TaticaJogadorResponseDTO dto = new TaticaJogadorResponseDTO();
        dto.setTaticaJogadorId(jogador.getTaticaJogadorId());
        dto.setTipo(jogador.getTipo());
        dto.setPosicaoX(jogador.getPosicaoX());
        dto.setPosicaoY(jogador.getPosicaoY());
        dto.setOrdemReserva(jogador.getOrdemReserva());

        Atleta atleta = jogador.getAtleta();
        if (atleta != null) {
            dto.setAtletaId(atleta.getAtletaId());
            dto.setNome(atleta.getNome());
            dto.setSobrenome(atleta.getSobrenome());
            dto.setPosicao(atleta.getPosicao());
            dto.setNacionalidade(atleta.getNacionalidade());
        }
        return dto;
    }
}
