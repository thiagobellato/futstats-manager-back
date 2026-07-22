package br.com.bellato.gerenciador_fifa.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoAtletaElencoDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoAtletaHistoricoClubeDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoAtualizarAtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoNovoAtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.dto.campeonato.CampeonatoTransferirAtletaRequestDTO;
import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoRepository;
import br.com.bellato.gerenciador_fifa.service.transferencia.AthleteTransferService;
import br.com.bellato.gerenciador_fifa.service.transferencia.CampeonatoAtletaIdentidade;

@Service
public class CampeonatoMercadoService {

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private CampeonatoClubeRepository campeonatoClubeRepository;

    @Autowired
    private CampeonatoAtletaRepository campeonatoAtletaRepository;

    @Autowired
    private AthleteTransferService athleteTransferService;

    @Transactional
    public CampeonatoAtletaElencoDTO criarNovoAtleta(Long campeonatoId, CampeonatoNovoAtletaRequestDTO request) {
        if (request == null || request.getCampeonatoClubeId() == null) {
            throw new CampeonatoBusinessException("Informe o clube do campeonato para o novo atleta.");
        }
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new CampeonatoBusinessException("Informe o nome do atleta.");
        }
        if (request.getPosicao() == null) {
            throw new CampeonatoBusinessException("Informe a posição do atleta.");
        }

        Campeonato campeonato = obterCampeonato(campeonatoId);
        validarCampeonatoPermiteMercado(campeonato);

        CampeonatoClube clube = campeonatoClubeRepository.findById(request.getCampeonatoClubeId())
                .orElseThrow(() -> new CampeonatoBusinessException("Clube não encontrado neste campeonato."));
        if (!Objects.equals(clube.getCampeonato().getCampeonatoId(), campeonatoId)) {
            throw new CampeonatoBusinessException("O clube informado não pertence a este campeonato.");
        }

        CampeonatoAtleta snapshot = new CampeonatoAtleta();
        snapshot.setCampeonato(campeonato);
        snapshot.setCampeonatoClube(clube);
        snapshot.setAtletaOrigemId(null);
        snapshot.setIdentidade(CampeonatoAtletaIdentidade.novaLocal());
        snapshot.setNome(request.getNome().trim());
        snapshot.setSobrenome(request.getSobrenome() != null && !request.getSobrenome().isBlank()
                ? request.getSobrenome().trim()
                : null);
        snapshot.setNacionalidade(request.getNacionalidade());
        snapshot.setPosicao(request.getPosicao());
        snapshot.setAtivo(true);
        snapshot.setDataInicio(LocalDate.now());
        snapshot.setGols(0);
        snapshot.setAssistencias(0);
        snapshot.setCartoesAmarelos(0);
        snapshot.setCartoesVermelhos(0);
        snapshot.setGolsContra(0);

        CampeonatoAtleta salvo = campeonatoAtletaRepository.save(snapshot);
        return toElencoDTO(salvo, List.of(salvo));
    }

    @Transactional
    public void transferirAtleta(Long campeonatoId, CampeonatoTransferirAtletaRequestDTO request) {
        if (request == null || request.getNovoCampeonatoClubeId() == null) {
            throw new CampeonatoBusinessException("Informe o clube de destino no campeonato.");
        }
        if (request.getCampeonatoAtletaId() == null && request.getAtletaOrigemId() == null) {
            throw new CampeonatoBusinessException(
                    "Informe o atleta do campeonato ou o atleta a importar para o campeonato.");
        }

        athleteTransferService.transferirNoCampeonato(
                campeonatoId,
                request.getCampeonatoAtletaId(),
                request.getAtletaOrigemId(),
                request.getNovoCampeonatoClubeId());
    }

    /**
     * Atualiza dados cadastrais apenas no snapshot do campeonato.
     * Não altera o atleta original no banco global.
     */
    @Transactional
    public CampeonatoAtletaElencoDTO atualizarAtleta(
            Long campeonatoId,
            Long campeonatoAtletaId,
            CampeonatoAtualizarAtletaRequestDTO request) {
        if (request == null) {
            throw new CampeonatoBusinessException("Informe os dados do atleta.");
        }
        if (request.getNome() == null || request.getNome().isBlank()) {
            throw new CampeonatoBusinessException("Informe o nome do atleta.");
        }
        if (request.getPosicao() == null) {
            throw new CampeonatoBusinessException("Informe a posição do atleta.");
        }

        Campeonato campeonato = obterCampeonato(campeonatoId);
        validarCampeonatoPermiteMercado(campeonato);

        CampeonatoAtleta vinculo = campeonatoAtletaRepository.findById(campeonatoAtletaId)
                .orElseThrow(() -> new CampeonatoBusinessException("Atleta não encontrado no campeonato."));
        if (!Objects.equals(vinculo.getCampeonato().getCampeonatoId(), campeonatoId)) {
            throw new CampeonatoBusinessException("O atleta informado não pertence a este campeonato.");
        }

        String identidade = CampeonatoAtletaIdentidade.garantir(vinculo);
        List<CampeonatoAtleta> vinculos = campeonatoAtletaRepository
                .findByCampeonatoCampeonatoIdAndIdentidade(campeonatoId, identidade);

        String nome = request.getNome().trim();
        String sobrenome = request.getSobrenome() != null && !request.getSobrenome().isBlank()
                ? request.getSobrenome().trim()
                : null;
        String nacionalidade = request.getNacionalidade() != null && !request.getNacionalidade().isBlank()
                ? request.getNacionalidade().trim()
                : null;

        for (CampeonatoAtleta v : vinculos) {
            v.setNome(nome);
            v.setSobrenome(sobrenome);
            v.setNacionalidade(nacionalidade);
            v.setPosicao(request.getPosicao());
        }
        campeonatoAtletaRepository.saveAll(vinculos);

        CampeonatoAtleta ativo = vinculos.stream()
                .filter(CampeonatoAtleta::isAtivo)
                .findFirst()
                .orElse(vinculo);
        return toElencoDTO(ativo, vinculos);
    }

    @Transactional
    public List<CampeonatoAtletaElencoDTO> listarElenco(Long campeonatoId) {
        obterCampeonato(campeonatoId);
        List<CampeonatoAtleta> todos = campeonatoAtletaRepository.findByCampeonatoCampeonatoId(campeonatoId);
        boolean precisaSalvar = false;
        for (CampeonatoAtleta atleta : todos) {
            String antes = atleta.getIdentidade();
            CampeonatoAtletaIdentidade.garantir(atleta);
            if (atleta.getAtivo() == null) {
                atleta.setAtivo(true);
                precisaSalvar = true;
            }
            if (!Objects.equals(antes, atleta.getIdentidade())) {
                precisaSalvar = true;
            }
        }
        if (precisaSalvar) {
            campeonatoAtletaRepository.saveAll(todos);
        }

        Map<String, List<CampeonatoAtleta>> porIdentidade = new LinkedHashMap<>();
        for (CampeonatoAtleta atleta : todos) {
            String identidade = CampeonatoAtletaIdentidade.garantir(atleta);
            porIdentidade.computeIfAbsent(identidade, k -> new ArrayList<>()).add(atleta);
        }

        List<CampeonatoAtletaElencoDTO> resultado = new ArrayList<>();
        for (List<CampeonatoAtleta> vinculos : porIdentidade.values()) {
            CampeonatoAtleta ativo = vinculos.stream()
                    .filter(CampeonatoAtleta::isAtivo)
                    .findFirst()
                    .orElse(vinculos.get(0));
            resultado.add(toElencoDTO(ativo, vinculos));
        }

        resultado.sort(Comparator.comparing(
                (CampeonatoAtletaElencoDTO d) -> d.getNome() == null ? "" : d.getNome(),
                String.CASE_INSENSITIVE_ORDER));
        return resultado;
    }

    private CampeonatoAtletaElencoDTO toElencoDTO(CampeonatoAtleta ativo, List<CampeonatoAtleta> vinculos) {
        CampeonatoAtletaElencoDTO dto = new CampeonatoAtletaElencoDTO();
        dto.setCampeonatoAtletaId(ativo.getCampeonatoAtletaId());
        dto.setAtletaOrigemId(ativo.getAtletaOrigemId());
        dto.setIdentidade(CampeonatoAtletaIdentidade.garantir(ativo));
        dto.setNome(ativo.getNome());
        dto.setSobrenome(ativo.getSobrenome());
        dto.setNacionalidade(ativo.getNacionalidade());
        dto.setPosicao(ativo.getPosicao());
        dto.setAtivo(ativo.isAtivo());
        dto.setDataInicio(ativo.getDataInicio());
        dto.setDataFim(ativo.getDataFim());
        if (ativo.getCampeonatoClube() != null) {
            dto.setCampeonatoClubeId(ativo.getCampeonatoClube().getCampeonatoClubeId());
            dto.setClubeNome(ativo.getCampeonatoClube().getNome());
        }

        int gols = 0;
        int assists = 0;
        int amarelos = 0;
        int vermelhos = 0;
        List<CampeonatoAtletaHistoricoClubeDTO> historico = new ArrayList<>();

        for (CampeonatoAtleta v : vinculos.stream()
                .sorted(Comparator.comparing(CampeonatoAtleta::isAtivo).reversed())
                .collect(Collectors.toList())) {
            gols += valor(v.getGols());
            assists += valor(v.getAssistencias());
            amarelos += valor(v.getCartoesAmarelos());
            vermelhos += valor(v.getCartoesVermelhos());

            CampeonatoAtletaHistoricoClubeDTO h = new CampeonatoAtletaHistoricoClubeDTO();
            h.setCampeonatoAtletaId(v.getCampeonatoAtletaId());
            h.setAtivo(v.isAtivo());
            h.setGols(valor(v.getGols()));
            h.setAssistencias(valor(v.getAssistencias()));
            h.setCartoesAmarelos(valor(v.getCartoesAmarelos()));
            h.setCartoesVermelhos(valor(v.getCartoesVermelhos()));
            if (v.getCampeonatoClube() != null) {
                h.setCampeonatoClubeId(v.getCampeonatoClube().getCampeonatoClubeId());
                h.setClubeNome(v.getCampeonatoClube().getNome());
            }
            historico.add(h);
        }

        dto.setGols(gols);
        dto.setAssistencias(assists);
        dto.setCartoesAmarelos(amarelos);
        dto.setCartoesVermelhos(vermelhos);
        dto.setHistorico(historico);
        return dto;
    }

    private Campeonato obterCampeonato(Long id) {
        return campeonatoRepository.findById(id)
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato não encontrado."));
    }

    private void validarCampeonatoPermiteMercado(Campeonato campeonato) {
        StatusCampeonato status = campeonato.getStatus();
        if (status == StatusCampeonato.FINALIZADO || status == StatusCampeonato.AGUARDANDO_FINALIZACAO) {
            throw new CampeonatoBusinessException("Campeonato finalizado não permite movimentações de elenco.");
        }
        if (status == StatusCampeonato.EM_CONFIGURACAO || status == StatusCampeonato.AGUARDANDO_INICIO) {
            throw new CampeonatoBusinessException("Inicie o campeonato antes de movimentar o elenco.");
        }
    }

    private int valor(Integer v) {
        return v == null ? 0 : v;
    }
}
