package br.com.bellato.gerenciador_fifa.model;

import br.com.bellato.gerenciador_fifa.enums.ClubRank;
import br.com.bellato.gerenciador_fifa.enums.ClubRank.ClubRankConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "campeonato_distribuicao_rank")
public class CampeonatoDistribuicaoRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campeonatoDistribuicaoRankId")
    private Long campeonatoDistribuicaoRankId;

    @ManyToOne
    @JoinColumn(name = "campeonato_id", nullable = false)
    private Campeonato campeonato;

    @Column(name = "campeonatoDistribuicaoRankRank", nullable = false)
    @Convert(converter = ClubRankConverter.class)
    private ClubRank rank;

    @Column(name = "campeonatoDistribuicaoRankQuantidade", nullable = false)
    private Integer quantidade;

    public CampeonatoDistribuicaoRank() {
    }

    public Long getCampeonatoDistribuicaoRankId() {
        return campeonatoDistribuicaoRankId;
    }

    public void setCampeonatoDistribuicaoRankId(Long campeonatoDistribuicaoRankId) {
        this.campeonatoDistribuicaoRankId = campeonatoDistribuicaoRankId;
    }

    public Campeonato getCampeonato() {
        return campeonato;
    }

    public void setCampeonato(Campeonato campeonato) {
        this.campeonato = campeonato;
    }

    public ClubRank getRank() {
        return rank;
    }

    public void setRank(ClubRank rank) {
        this.rank = rank;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
