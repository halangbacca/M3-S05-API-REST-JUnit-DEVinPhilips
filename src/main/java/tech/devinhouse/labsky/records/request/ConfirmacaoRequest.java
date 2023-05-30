package tech.devinhouse.labsky.records.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConfirmacaoRequest(
        @NotBlank
        String cpf,
        @NotBlank
        String assento,
        @NotNull
        Boolean malasDespachadas,
        String eticket,
        LocalDateTime dataHoraConfirmacao
) {

}
