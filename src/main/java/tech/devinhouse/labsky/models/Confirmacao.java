package tech.devinhouse.labsky.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Confirmacao {
    private String eticket;
    private String assento;
    @Column(name = "data_hora_confirmacao")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHoraConfirmacao;
    @Column(name = "malas_despachadas")
    private Boolean malasDespachadas;
}
