package tech.devinhouse.labsky.records.response;

import tech.devinhouse.labsky.models.Passageiro;

import java.time.LocalDateTime;

public record ConfirmacaoResponse(String eticket, LocalDateTime dataHoraConfirmacao) {
    public ConfirmacaoResponse(Passageiro passageiro) {
        this(passageiro.getEticket(), passageiro.getDataHoraConfirmacao());
    }
}
