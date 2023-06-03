package tech.devinhouse.labsky.records.response;

import tech.devinhouse.labsky.models.Confirmacao;
import tech.devinhouse.labsky.models.Passageiro;

import java.time.LocalDateTime;

public record ConfirmacaoResponse(String eticket, LocalDateTime dataHoraConfirmacao) {
    public ConfirmacaoResponse(Passageiro passageiro) {
        this(passageiro.getConfirmacao().getEticket(), passageiro.getConfirmacao().getDataHoraConfirmacao());
    }

    public ConfirmacaoResponse(Confirmacao confirmacao) {
        this(confirmacao.getEticket(), confirmacao.getDataHoraConfirmacao());
    }

}
