package br.com.bellato.gerenciador_fifa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.bellato.gerenciador_fifa.dto.hall.HallBuscaResultadoDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallCampeonatoDetalheDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallCampeonatoResumoDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallClubePerfilDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallCompetidorPerfilDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallJogadorPerfilDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallRankingPageDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallRecordesDTO;
import br.com.bellato.gerenciador_fifa.dto.hall.HallTimelineItemDTO;
import br.com.bellato.gerenciador_fifa.service.HallDaFamaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/hall-da-fama")
@Tag(name = "Hall da Fama")
public class HallDaFamaController {

    @Autowired
    private HallDaFamaService hallDaFamaService;

    @GetMapping("/campeonatos")
    @Operation(summary = "Listar campeonatos finalizados (histórico)")
    public ResponseEntity<List<HallCampeonatoResumoDTO>> listarCampeonatos(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Integer ano,
            @RequestParam(required = false) Integer quantidadeClubes,
            @RequestParam(required = false) String competidor,
            @RequestParam(required = false) String clube,
            @RequestParam(required = false) String campeao,
            @RequestParam(required = false) String ranking) {
        return ResponseEntity.ok(hallDaFamaService.listarCampeonatos(
                busca, ano, quantidadeClubes, competidor, clube, campeao, ranking));
    }

    @GetMapping("/campeonatos/{id}")
    @Operation(summary = "Dashboard histórico completo de um campeonato finalizado")
    public ResponseEntity<HallCampeonatoDetalheDTO> obterCampeonato(@PathVariable Long id) {
        return ResponseEntity.ok(hallDaFamaService.obterCampeonato(id));
    }

    @GetMapping("/timeline")
    @Operation(summary = "Linha do tempo dos campeonatos finalizados")
    public ResponseEntity<List<HallTimelineItemDTO>> obterTimeline() {
        return ResponseEntity.ok(hallDaFamaService.obterTimeline());
    }

    @GetMapping("/recordes")
    @Operation(summary = "Recordes de competidores (sem paginação)")
    public ResponseEntity<HallRecordesDTO> obterRecordes() {
        return ResponseEntity.ok(hallDaFamaService.obterRecordes());
    }

    @GetMapping("/recordes/ranking")
    @Operation(summary = "Ranking paginado de clubes ou jogadores do Hall da Fama")
    public ResponseEntity<HallRankingPageDTO> obterRankingPaginado(
            @RequestParam String chave,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String busca) {
        return ResponseEntity.ok(hallDaFamaService.obterRankingPaginado(chave, page, size, busca));
    }

    @GetMapping("/clube/{id}")
    @Operation(summary = "Histórico individual de um clube")
    public ResponseEntity<HallClubePerfilDTO> obterPerfilClube(@PathVariable Long id) {
        return ResponseEntity.ok(hallDaFamaService.obterPerfilClube(id));
    }

    @GetMapping("/competidor")
    @Operation(summary = "Histórico individual de um competidor")
    public ResponseEntity<HallCompetidorPerfilDTO> obterPerfilCompetidor(@RequestParam String nome) {
        return ResponseEntity.ok(hallDaFamaService.obterPerfilCompetidor(nome));
    }

    @GetMapping("/jogador/{id}")
    @Operation(summary = "Histórico individual de um jogador")
    public ResponseEntity<HallJogadorPerfilDTO> obterPerfilJogador(@PathVariable Long id) {
        return ResponseEntity.ok(hallDaFamaService.obterPerfilJogador(id));
    }

    @GetMapping("/busca")
    @Operation(summary = "Busca global no Hall da Fama")
    public ResponseEntity<HallBuscaResultadoDTO> buscar(@RequestParam String q) {
        return ResponseEntity.ok(hallDaFamaService.buscar(q));
    }
}
