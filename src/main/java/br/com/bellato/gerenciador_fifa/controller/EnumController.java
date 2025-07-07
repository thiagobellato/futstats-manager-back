package br.com.bellato.gerenciador_fifa.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.posicao.PosicaoDTO;
import br.com.bellato.gerenciador_fifa.enums.PosicaoFutebol;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/posicoes")
    public List<PosicaoDTO> getTodasPosicoes() {
        return Arrays.stream(PosicaoFutebol.values())
                .map(p -> new PosicaoDTO(p.getSiglaPosicao(), p.getDescricaoPosicao()))
                .collect(Collectors.toList());
    }
}
