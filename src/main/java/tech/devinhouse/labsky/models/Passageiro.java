package tech.devinhouse.labsky.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tech.devinhouse.labsky.enums.Classificacao;

import java.time.LocalDate;

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
    private Classificacao classificacao = Classificacao.ASSOCIADO;
    private Integer milhas;
    @Embedded
    private Confirmacao confirmacao;

    public Passageiro(String cpf, String nome) {
        this.cpf = cpf;
        this.nome = nome;
    }

    public Passageiro(String cpf, String nome, LocalDate dataNascimento, Classificacao classificacao, Integer milhas) {
        this.cpf = cpf;
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.classificacao = classificacao;
        this.milhas = milhas;
    }

}
