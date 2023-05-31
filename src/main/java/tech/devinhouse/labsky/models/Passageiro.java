package tech.devinhouse.labsky.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.devinhouse.labsky.enums.Classificacao;
import tech.devinhouse.labsky.records.request.ConfirmacaoRequest;
import tech.devinhouse.labsky.records.response.ConsultaCPFResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passageiros")
public class Passageiro {
    @Id
    @Column(unique = true)
    private String cpf;
    private String nome;
    @Column(name = "data_nascimento")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;
    @Enumerated(EnumType.STRING)
    private Classificacao classificacao;
    private Integer milhas;
    private String eticket;
    private String assento;
    @Column(name = "data_hora_confirmacao")
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHoraConfirmacao;
    @Column(name = "malas_despachadas")
    private Boolean malasDespachadas;

    public Passageiro(ConfirmacaoRequest request) {
        this.cpf = request.cpf();
        this.assento = request.assento();
        this.malasDespachadas = request.malasDespachadas();
        this.eticket = request.eticket();
        this.dataHoraConfirmacao = request.dataHoraConfirmacao();
    }

    public Passageiro(ConsultaCPFResponse response) {
        this.cpf = response.cpf();
        this.nome = response.nome();
        this.dataNascimento = response.dataNascimento();
        this.classificacao = response.classificacao();
        this.milhas = response.milhas();
    }

    public Passageiro(String cpf, String nome) {
        this.cpf = cpf;
        this.nome = nome;
    }

    public Passageiro(String eticket, LocalDateTime dataHoraConfirmacao) {
        this.eticket = eticket;
        this.dataHoraConfirmacao = dataHoraConfirmacao;
    }

    public Passageiro(String cpf, String nome, LocalDate dataNascimento, Classificacao classificacao, Integer milhas) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.classificacao = classificacao;
        this.milhas = milhas;
    }
}
