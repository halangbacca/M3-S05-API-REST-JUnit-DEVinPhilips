package tech.devinhouse.labsky.records.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import tech.devinhouse.labsky.enums.Classificacao;
import tech.devinhouse.labsky.models.Passageiro;

import java.time.LocalDate;

public record ConsultaCPFResponse(
        String cpf,
        String nome,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,
        Classificacao classificacao,
        Integer milhas) {
    public ConsultaCPFResponse(Passageiro passageiro) {
        this(passageiro.getCpf(), passageiro.getNome(), passageiro.getDataNascimento(), passageiro.getClassificacao(), passageiro.getMilhas());
    }
}
