package br.com.bellato.gerenciador_fifa.dto.hall;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class HallRankingPageDTO {

    private String chave;
    private List<HallRecordeItemDTO> itens = new ArrayList<>();
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public List<HallRecordeItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<HallRecordeItemDTO> itens) {
        this.itens = itens;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
