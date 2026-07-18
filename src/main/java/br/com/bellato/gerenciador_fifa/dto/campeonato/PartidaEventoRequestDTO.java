package br.com.bellato.gerenciador_fifa.dto.campeonato;

import br.com.bellato.gerenciador_fifa.enums.TipoEventoPartida;

public class PartidaEventoRequestDTO {

    private TipoEventoPartida tipo;
    private Long campeonatoAtletaId;

    public TipoEventoPartida getTipo() {
        return tipo;
    }

    public void setTipo(TipoEventoPartida tipo) {
        this.tipo = tipo;
    }

    public Long getCampeonatoAtletaId() {
        return campeonatoAtletaId;
    }

    public void setCampeonatoAtletaId(Long campeonatoAtletaId) {
        this.campeonatoAtletaId = campeonatoAtletaId;
    }
}
