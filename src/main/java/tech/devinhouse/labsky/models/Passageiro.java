package tech.devinhouse.labsky.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import tech.devinhouse.labsky.enums.Fidelidade;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "passageiros")
public class Passageiro {
    @Id
    @CPF
    @Column(unique = true)
    private String cpf;
    private String nome;
    @Column(name = "data_nascimento")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataNascimento;
    @Enumerated(EnumType.STRING)
    private Fidelidade fidelidade;
    private Integer milhas;
}
