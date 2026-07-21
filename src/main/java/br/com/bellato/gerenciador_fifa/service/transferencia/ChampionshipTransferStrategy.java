package br.com.bellato.gerenciador_fifa.service.transferencia;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.com.bellato.gerenciador_fifa.enums.StatusCampeonato;
import br.com.bellato.gerenciador_fifa.exception.CampeonatoBusinessException;
import br.com.bellato.gerenciador_fifa.model.Atleta;
import br.com.bellato.gerenciador_fifa.model.Campeonato;
import br.com.bellato.gerenciador_fifa.model.CampeonatoAtleta;
import br.com.bellato.gerenciador_fifa.model.CampeonatoClube;
import br.com.bellato.gerenciador_fifa.repository.AtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoAtletaRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoClubeRepository;
import br.com.bellato.gerenciador_fifa.repository.CampeonatoRepository;

/**
 * Estratégia que altera apenas snapshots do campeonato.
 * Nunca toca no banco global (Atleta, EstatisticaAtleta, Clube).
 */
@Component
public class ChampionshipTransferStrategy implements AthleteTransferStrategy {

    @Autowired
    private CampeonatoRepository campeonatoRepository;

    @Autowired
    private CampeonatoAtletaRepository campeonatoAtletaRepository;

    @Autowired
    private CampeonatoClubeRepository campeonatoClubeRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    @Override
    public AthleteTransferCommand.Escopo getEscopo() {
        return AthleteTransferCommand.Escopo.CAMPEONATO;
    }

    @Override
    @Transactional
    public void transferir(AthleteTransferCommand command) {
        if (command.getCampeonatoId() == null || command.getNovoCampeonatoClubeId() == null) {
            throw new CampeonatoBusinessException("Informe o campeonato e o clube de destino.");
        }

        Campeonato campeonato = campeonatoRepository.findById(command.getCampeonatoId())
                .orElseThrow(() -> new CampeonatoBusinessException("Campeonato não encontrado."));
        validarCampeonatoPermiteMercado(campeonato);

        CampeonatoClube destino = campeonatoClubeRepository.findById(command.getNovoCampeonatoClubeId())
                .orElseThrow(() -> new CampeonatoBusinessException("Clube de destino não encontrado no campeonato."));
        if (!Objects.equals(destino.getCampeonato().getCampeonatoId(), campeonato.getCampeonatoId())) {
            throw new CampeonatoBusinessException("O clube de destino não pertence a este campeonato.");
        }

        CampeonatoAtleta vinculoAtivo = resolverVinculoOrigem(command, campeonato);

        if (vinculoAtivo != null) {
            if (Objects.equals(vinculoAtivo.getCampeonatoClube().getCampeonatoClubeId(),
                    destino.getCampeonatoClubeId())) {
                throw new CampeonatoBusinessException("O atleta já pertence a este clube no campeonato.");
            }
            encerrarVinculo(vinculoAtivo);
            campeonatoAtletaRepository.save(vinculoAtivo);
            campeonatoAtletaRepository.save(criarNovoVinculo(vinculoAtivo, destino));
            return;
        }

        if (command.getAtletaGlobalId() == null) {
            throw new CampeonatoBusinessException(
                    "Informe o vínculo do campeonato ou o atleta global a ser importado.");
        }

        Atleta atletaGlobal = atletaRepository.findById(command.getAtletaGlobalId())
                .orElseThrow(() -> new CampeonatoBusinessException("Atleta global não encontrado."));

        List<CampeonatoAtleta> existentes = campeonatoAtletaRepository
                .findByCampeonatoCampeonatoIdAndAtletaOrigemId(
                        campeonato.getCampeonatoId(), atletaGlobal.getAtletaId());
        for (CampeonatoAtleta existente : existentes) {
            if (existente.isAtivo()) {
                if (Objects.equals(existente.getCampeonatoClube().getCampeonatoClubeId(),
                        destino.getCampeonatoClubeId())) {
                    throw new CampeonatoBusinessException("O atleta já pertence a este clube no campeonato.");
                }
                encerrarVinculo(existente);
                campeonatoAtletaRepository.save(existente);
                campeonatoAtletaRepository.save(criarNovoVinculo(existente, destino));
                return;
            }
        }

        // Já existiu snapshot neste campeonato (vínculos encerrados): reabre a partir do último,
        // preservando identidade e dados do snapshot — sem alterar o atleta global.
        if (!existentes.isEmpty()) {
            CampeonatoAtleta ultimo = existentes.get(existentes.size() - 1);
            campeonatoAtletaRepository.save(criarNovoVinculo(ultimo, destino));
            return;
        }

        CampeonatoAtleta novo = new CampeonatoAtleta();
        novo.setCampeonato(campeonato);
        novo.setCampeonatoClube(destino);
        novo.setAtletaOrigemId(atletaGlobal.getAtletaId());
        novo.setIdentidade(CampeonatoAtletaIdentidade.paraAtletaGlobal(atletaGlobal.getAtletaId()));
        novo.setNome(atletaGlobal.getNome());
        novo.setSobrenome(atletaGlobal.getSobrenome());
        novo.setDataDeNascimento(atletaGlobal.getDataDeNascimento());
        novo.setNacionalidade(atletaGlobal.getNacionalidade());
        novo.setPosicao(atletaGlobal.getPosicao());
        novo.setAtivo(true);
        novo.setDataInicio(LocalDate.now());
        novo.setGols(0);
        novo.setAssistencias(0);
        novo.setCartoesAmarelos(0);
        novo.setCartoesVermelhos(0);
        campeonatoAtletaRepository.save(novo);
    }

    private CampeonatoAtleta resolverVinculoOrigem(AthleteTransferCommand command, Campeonato campeonato) {
        if (command.getCampeonatoAtletaId() != null) {
            CampeonatoAtleta vinculo = campeonatoAtletaRepository.findById(command.getCampeonatoAtletaId())
                    .orElseThrow(() -> new CampeonatoBusinessException(
                            "Vínculo do atleta não encontrado no campeonato."));
            if (!Objects.equals(vinculo.getCampeonato().getCampeonatoId(), campeonato.getCampeonatoId())) {
                throw new CampeonatoBusinessException("O vínculo informado não pertence a este campeonato.");
            }
            if (!vinculo.isAtivo()) {
                throw new CampeonatoBusinessException("Somente o vínculo ativo do atleta pode ser transferido.");
            }
            return vinculo;
        }

        if (command.getAtletaGlobalId() != null) {
            return campeonatoAtletaRepository
                    .findByCampeonatoCampeonatoIdAndAtletaOrigemIdAndAtivoTrue(
                            campeonato.getCampeonatoId(), command.getAtletaGlobalId())
                    .orElse(null);
        }
        return null;
    }

    private void encerrarVinculo(CampeonatoAtleta vinculo) {
        CampeonatoAtletaIdentidade.garantir(vinculo);
        vinculo.setAtivo(false);
        vinculo.setDataFim(LocalDate.now());
    }

    private CampeonatoAtleta criarNovoVinculo(CampeonatoAtleta origem, CampeonatoClube destino) {
        CampeonatoAtleta novo = new CampeonatoAtleta();
        novo.setCampeonato(origem.getCampeonato());
        novo.setCampeonatoClube(destino);
        novo.setAtletaOrigemId(origem.getAtletaOrigemId());
        novo.setIdentidade(CampeonatoAtletaIdentidade.garantir(origem));
        novo.setNome(origem.getNome());
        novo.setSobrenome(origem.getSobrenome());
        novo.setDataDeNascimento(origem.getDataDeNascimento());
        novo.setNacionalidade(origem.getNacionalidade());
        novo.setPosicao(origem.getPosicao());
        novo.setAtivo(true);
        novo.setDataInicio(LocalDate.now());
        novo.setGols(0);
        novo.setAssistencias(0);
        novo.setCartoesAmarelos(0);
        novo.setCartoesVermelhos(0);
        return novo;
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
}
