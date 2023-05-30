package tech.devinhouse.labsky.models;

import tech.devinhouse.labsky.enums.Fidelidade;

import java.time.LocalDate;

public class Passageiro {
    private String cpf;
    private String nome;
    private LocalDate dataNascimento;
    private Fidelidade fidelidade;
    private Integer milhas;
}
