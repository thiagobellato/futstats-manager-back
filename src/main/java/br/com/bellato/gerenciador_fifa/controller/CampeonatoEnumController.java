package br.com.bellato.gerenciador_fifa.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.posicao.PosicaoDTO;
import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Enums do Campeonato")
public class CampeonatoEnumController {

    @GetMapping("/enums/ranks")
    public List<PosicaoDTO> getTodosRanks() {
        return Arrays.stream(ClubRank.values())
                .map(r -> new PosicaoDTO(r.getSigla(), r.getDescricao()))
                .collect(Collectors.toList());
    }

    @GetMapping("/enums/status-campeonato")
    public List<PosicaoDTO> getTodosStatusCampeonato() {
        return Arrays.stream(StatusCampeonato.values())
                .map(s -> new PosicaoDTO(s.getCodigo(), s.getDescricao()))
                .collect(Collectors.toList());
    }
}
