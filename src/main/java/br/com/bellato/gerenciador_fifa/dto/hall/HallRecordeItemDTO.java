package br.com.bellato.gerenciador_fifa.dto.hall;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallRecordeItemDTO {

    private Long id;
    private String nome;
    private String detalhe;
    private Number valor;
    private String valorLabel;

    public HallRecordeItemDTO() {
    }

    public HallRecordeItemDTO(Long id, String nome, String detalhe, Number valor, String valorLabel) {
        this.id = id;
        this.nome = nome;
        this.detalhe = detalhe;
        this.valor = valor;
        this.valorLabel = valorLabel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }

    public Number getValor() {
        return valor;
    }

    public void setValor(Number valor) {
        this.valor = valor;
    }

    public String getValorLabel() {
        return valorLabel;
    }

    public void setValorLabel(String valorLabel) {
        this.valorLabel = valorLabel;
    }
}
