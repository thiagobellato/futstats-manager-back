package br.com.bellato.gerenciador_fifa.service.transferencia;

public interface AthleteTransferStrategy {

    AthleteTransferCommand.Escopo getEscopo();

    void transferir(AthleteTransferCommand command);
}
