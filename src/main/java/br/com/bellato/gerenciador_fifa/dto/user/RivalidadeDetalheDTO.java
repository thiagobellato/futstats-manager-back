package br.com.bellato.gerenciador_fifa.dto.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RivalidadeDetalheDTO {

    private Long rivalidadeId;

    private Long usuarioAId;
    private String usuarioAUsername;
    private String usuarioADisplayName;

    private Long usuarioBId;
    private String usuarioBUsername;
    private String usuarioBDisplayName;

    private int confrontos;
    private int vitoriasUsuarioA;
    private int vitoriasUsuarioB;
    private int empates;
    private int golsUsuarioA;
    private int golsUsuarioB;
    private int saldoUsuarioA;
    private Double aproveitamentoUsuarioA;
    private Double aproveitamentoUsuarioB;

    private int campeonatosDisputados;
    private int titulosUsuarioA;
    private int titulosUsuarioB;

    private Integer maiorGoleadaAGolsPro;
    private Integer maiorGoleadaAGolsContra;
    private Integer maiorGoleadaAMargem;
    private String maiorGoleadaAPlacar;
    private String maiorGoleadaACampeonatoNome;

    private Integer maiorGoleadaBGolsPro;
    private Integer maiorGoleadaBGolsContra;
    private Integer maiorGoleadaBMargem;
    private String maiorGoleadaBPlacar;
    private String maiorGoleadaBCampeonatoNome;

    private int maiorSequenciaVitoriasA;
    private int maiorSequenciaVitoriasB;
    private int sequenciaAtual;
    private Long sequenciaAtualUserId;
    private String sequenciaAtualUsername;

    private LocalDateTime dataUltimoConfronto;
    private Long ultimoVencedorUserId;
    private String ultimoVencedorUsername;

    private List<ConfrontoTimelineDTO> campeonatos = new ArrayList<>();
    private List<ConfrontoTimelineDTO> linhaDoTempo = new ArrayList<>();

    public Long getRivalidadeId() {
        return rivalidadeId;
    }

    public void setRivalidadeId(Long rivalidadeId) {
        this.rivalidadeId = rivalidadeId;
    }

    public Long getUsuarioAId() {
        return usuarioAId;
    }

    public void setUsuarioAId(Long usuarioAId) {
        this.usuarioAId = usuarioAId;
    }

    public String getUsuarioAUsername() {
        return usuarioAUsername;
    }

    public void setUsuarioAUsername(String usuarioAUsername) {
        this.usuarioAUsername = usuarioAUsername;
    }

    public String getUsuarioADisplayName() {
        return usuarioADisplayName;
    }

    public void setUsuarioADisplayName(String usuarioADisplayName) {
        this.usuarioADisplayName = usuarioADisplayName;
    }

    public Long getUsuarioBId() {
        return usuarioBId;
    }

    public void setUsuarioBId(Long usuarioBId) {
        this.usuarioBId = usuarioBId;
    }

    public String getUsuarioBUsername() {
        return usuarioBUsername;
    }

    public void setUsuarioBUsername(String usuarioBUsername) {
        this.usuarioBUsername = usuarioBUsername;
    }

    public String getUsuarioBDisplayName() {
        return usuarioBDisplayName;
    }

    public void setUsuarioBDisplayName(String usuarioBDisplayName) {
        this.usuarioBDisplayName = usuarioBDisplayName;
    }

    public int getConfrontos() {
        return confrontos;
    }

    public void setConfrontos(int confrontos) {
        this.confrontos = confrontos;
    }

    public int getVitoriasUsuarioA() {
        return vitoriasUsuarioA;
    }

    public void setVitoriasUsuarioA(int vitoriasUsuarioA) {
        this.vitoriasUsuarioA = vitoriasUsuarioA;
    }

    public int getVitoriasUsuarioB() {
        return vitoriasUsuarioB;
    }

    public void setVitoriasUsuarioB(int vitoriasUsuarioB) {
        this.vitoriasUsuarioB = vitoriasUsuarioB;
    }

    public int getEmpates() {
        return empates;
    }

    public void setEmpates(int empates) {
        this.empates = empates;
    }

    public int getGolsUsuarioA() {
        return golsUsuarioA;
    }

    public void setGolsUsuarioA(int golsUsuarioA) {
        this.golsUsuarioA = golsUsuarioA;
    }

    public int getGolsUsuarioB() {
        return golsUsuarioB;
    }

    public void setGolsUsuarioB(int golsUsuarioB) {
        this.golsUsuarioB = golsUsuarioB;
    }

    public int getSaldoUsuarioA() {
        return saldoUsuarioA;
    }

    public void setSaldoUsuarioA(int saldoUsuarioA) {
        this.saldoUsuarioA = saldoUsuarioA;
    }

    public Double getAproveitamentoUsuarioA() {
        return aproveitamentoUsuarioA;
    }

    public void setAproveitamentoUsuarioA(Double aproveitamentoUsuarioA) {
        this.aproveitamentoUsuarioA = aproveitamentoUsuarioA;
    }

    public Double getAproveitamentoUsuarioB() {
        return aproveitamentoUsuarioB;
    }

    public void setAproveitamentoUsuarioB(Double aproveitamentoUsuarioB) {
        this.aproveitamentoUsuarioB = aproveitamentoUsuarioB;
    }

    public int getCampeonatosDisputados() {
        return campeonatosDisputados;
    }

    public void setCampeonatosDisputados(int campeonatosDisputados) {
        this.campeonatosDisputados = campeonatosDisputados;
    }

    public int getTitulosUsuarioA() {
        return titulosUsuarioA;
    }

    public void setTitulosUsuarioA(int titulosUsuarioA) {
        this.titulosUsuarioA = titulosUsuarioA;
    }

    public int getTitulosUsuarioB() {
        return titulosUsuarioB;
    }

    public void setTitulosUsuarioB(int titulosUsuarioB) {
        this.titulosUsuarioB = titulosUsuarioB;
    }

    public Integer getMaiorGoleadaAGolsPro() {
        return maiorGoleadaAGolsPro;
    }

    public void setMaiorGoleadaAGolsPro(Integer maiorGoleadaAGolsPro) {
        this.maiorGoleadaAGolsPro = maiorGoleadaAGolsPro;
    }

    public Integer getMaiorGoleadaAGolsContra() {
        return maiorGoleadaAGolsContra;
    }

    public void setMaiorGoleadaAGolsContra(Integer maiorGoleadaAGolsContra) {
        this.maiorGoleadaAGolsContra = maiorGoleadaAGolsContra;
    }

    public Integer getMaiorGoleadaAMargem() {
        return maiorGoleadaAMargem;
    }

    public void setMaiorGoleadaAMargem(Integer maiorGoleadaAMargem) {
        this.maiorGoleadaAMargem = maiorGoleadaAMargem;
    }

    public String getMaiorGoleadaAPlacar() {
        return maiorGoleadaAPlacar;
    }

    public void setMaiorGoleadaAPlacar(String maiorGoleadaAPlacar) {
        this.maiorGoleadaAPlacar = maiorGoleadaAPlacar;
    }

    public String getMaiorGoleadaACampeonatoNome() {
        return maiorGoleadaACampeonatoNome;
    }

    public void setMaiorGoleadaACampeonatoNome(String maiorGoleadaACampeonatoNome) {
        this.maiorGoleadaACampeonatoNome = maiorGoleadaACampeonatoNome;
    }

    public Integer getMaiorGoleadaBGolsPro() {
        return maiorGoleadaBGolsPro;
    }

    public void setMaiorGoleadaBGolsPro(Integer maiorGoleadaBGolsPro) {
        this.maiorGoleadaBGolsPro = maiorGoleadaBGolsPro;
    }

    public Integer getMaiorGoleadaBGolsContra() {
        return maiorGoleadaBGolsContra;
    }

    public void setMaiorGoleadaBGolsContra(Integer maiorGoleadaBGolsContra) {
        this.maiorGoleadaBGolsContra = maiorGoleadaBGolsContra;
    }

    public Integer getMaiorGoleadaBMargem() {
        return maiorGoleadaBMargem;
    }

    public void setMaiorGoleadaBMargem(Integer maiorGoleadaBMargem) {
        this.maiorGoleadaBMargem = maiorGoleadaBMargem;
    }

    public String getMaiorGoleadaBPlacar() {
        return maiorGoleadaBPlacar;
    }

    public void setMaiorGoleadaBPlacar(String maiorGoleadaBPlacar) {
        this.maiorGoleadaBPlacar = maiorGoleadaBPlacar;
    }

    public String getMaiorGoleadaBCampeonatoNome() {
        return maiorGoleadaBCampeonatoNome;
    }

    public void setMaiorGoleadaBCampeonatoNome(String maiorGoleadaBCampeonatoNome) {
        this.maiorGoleadaBCampeonatoNome = maiorGoleadaBCampeonatoNome;
    }

    public int getMaiorSequenciaVitoriasA() {
        return maiorSequenciaVitoriasA;
    }

    public void setMaiorSequenciaVitoriasA(int maiorSequenciaVitoriasA) {
        this.maiorSequenciaVitoriasA = maiorSequenciaVitoriasA;
    }

    public int getMaiorSequenciaVitoriasB() {
        return maiorSequenciaVitoriasB;
    }

    public void setMaiorSequenciaVitoriasB(int maiorSequenciaVitoriasB) {
        this.maiorSequenciaVitoriasB = maiorSequenciaVitoriasB;
    }

    public int getSequenciaAtual() {
        return sequenciaAtual;
    }

    public void setSequenciaAtual(int sequenciaAtual) {
        this.sequenciaAtual = sequenciaAtual;
    }

    public Long getSequenciaAtualUserId() {
        return sequenciaAtualUserId;
    }

    public void setSequenciaAtualUserId(Long sequenciaAtualUserId) {
        this.sequenciaAtualUserId = sequenciaAtualUserId;
    }

    public String getSequenciaAtualUsername() {
        return sequenciaAtualUsername;
    }

    public void setSequenciaAtualUsername(String sequenciaAtualUsername) {
        this.sequenciaAtualUsername = sequenciaAtualUsername;
    }

    public LocalDateTime getDataUltimoConfronto() {
        return dataUltimoConfronto;
    }

    public void setDataUltimoConfronto(LocalDateTime dataUltimoConfronto) {
        this.dataUltimoConfronto = dataUltimoConfronto;
    }

    public Long getUltimoVencedorUserId() {
        return ultimoVencedorUserId;
    }

    public void setUltimoVencedorUserId(Long ultimoVencedorUserId) {
        this.ultimoVencedorUserId = ultimoVencedorUserId;
    }

    public String getUltimoVencedorUsername() {
        return ultimoVencedorUsername;
    }

    public void setUltimoVencedorUsername(String ultimoVencedorUsername) {
        this.ultimoVencedorUsername = ultimoVencedorUsername;
    }

    public List<ConfrontoTimelineDTO> getCampeonatos() {
        return campeonatos;
    }

    public void setCampeonatos(List<ConfrontoTimelineDTO> campeonatos) {
        this.campeonatos = campeonatos;
    }

    public List<ConfrontoTimelineDTO> getLinhaDoTempo() {
        return linhaDoTempo;
    }

    public void setLinhaDoTempo(List<ConfrontoTimelineDTO> linhaDoTempo) {
        this.linhaDoTempo = linhaDoTempo;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ConfrontoTimelineDTO {
        private Long historicoId;
        private Long campeonatoId;
        private String nomeCampeonato;
        private LocalDateTime dataFinalizacao;
        private int partidasDisputadas;
        private int golsParticipanteA;
        private int golsParticipanteB;
        private String placarGeral;
        private Long campeaoUserId;
        private String campeaoUsername;
        private String campeaoDisplayName;
        private String clubeCampeaoNome;
        private Integer maiorGoleadaGolsA;
        private Integer maiorGoleadaGolsB;

        public Long getHistoricoId() {
            return historicoId;
        }

        public void setHistoricoId(Long historicoId) {
            this.historicoId = historicoId;
        }

        public Long getCampeonatoId() {
            return campeonatoId;
        }

        public void setCampeonatoId(Long campeonatoId) {
            this.campeonatoId = campeonatoId;
        }

        public String getNomeCampeonato() {
            return nomeCampeonato;
        }

        public void setNomeCampeonato(String nomeCampeonato) {
            this.nomeCampeonato = nomeCampeonato;
        }

        public LocalDateTime getDataFinalizacao() {
            return dataFinalizacao;
        }

        public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
            this.dataFinalizacao = dataFinalizacao;
        }

        public int getPartidasDisputadas() {
            return partidasDisputadas;
        }

        public void setPartidasDisputadas(int partidasDisputadas) {
            this.partidasDisputadas = partidasDisputadas;
        }

        public int getGolsParticipanteA() {
            return golsParticipanteA;
        }

        public void setGolsParticipanteA(int golsParticipanteA) {
            this.golsParticipanteA = golsParticipanteA;
        }

        public int getGolsParticipanteB() {
            return golsParticipanteB;
        }

        public void setGolsParticipanteB(int golsParticipanteB) {
            this.golsParticipanteB = golsParticipanteB;
        }

        public String getPlacarGeral() {
            return placarGeral;
        }

        public void setPlacarGeral(String placarGeral) {
            this.placarGeral = placarGeral;
        }

        public Long getCampeaoUserId() {
            return campeaoUserId;
        }

        public void setCampeaoUserId(Long campeaoUserId) {
            this.campeaoUserId = campeaoUserId;
        }

        public String getCampeaoUsername() {
            return campeaoUsername;
        }

        public void setCampeaoUsername(String campeaoUsername) {
            this.campeaoUsername = campeaoUsername;
        }

        public String getCampeaoDisplayName() {
            return campeaoDisplayName;
        }

        public void setCampeaoDisplayName(String campeaoDisplayName) {
            this.campeaoDisplayName = campeaoDisplayName;
        }

        public String getClubeCampeaoNome() {
            return clubeCampeaoNome;
        }

        public void setClubeCampeaoNome(String clubeCampeaoNome) {
            this.clubeCampeaoNome = clubeCampeaoNome;
        }

        public Integer getMaiorGoleadaGolsA() {
            return maiorGoleadaGolsA;
        }

        public void setMaiorGoleadaGolsA(Integer maiorGoleadaGolsA) {
            this.maiorGoleadaGolsA = maiorGoleadaGolsA;
        }

        public Integer getMaiorGoleadaGolsB() {
            return maiorGoleadaGolsB;
        }

        public void setMaiorGoleadaGolsB(Integer maiorGoleadaGolsB) {
            this.maiorGoleadaGolsB = maiorGoleadaGolsB;
        }
    }
}
