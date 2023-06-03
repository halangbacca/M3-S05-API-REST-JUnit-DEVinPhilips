package tech.devinhouse.labsky.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.devinhouse.labsky.enums.Classificacao;
import tech.devinhouse.labsky.models.Confirmacao;
import tech.devinhouse.labsky.models.Passageiro;
import tech.devinhouse.labsky.records.request.ConfirmacaoRequest;
import tech.devinhouse.labsky.records.response.ConfirmacaoResponse;
import tech.devinhouse.labsky.records.response.ConsultaCPFResponse;
import tech.devinhouse.labsky.repositories.PassageiroRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PassageiroService {

    @Autowired
    private PassageiroRepository repository;

    public List<Passageiro> listaPassageiros() {
        return repository.findAll();
    }

    public ConsultaCPFResponse listaPassageiroPeloCpf(String cpf) {
        var passageiro = repository.findById(cpf).orElseThrow(() -> new EntityNotFoundException("Passageiro não encontrado!"));
        return new ConsultaCPFResponse(passageiro);
    }

    public ConfirmacaoResponse confirmacao(ConfirmacaoRequest request) {
        Passageiro passageiro = repository.findById(request.cpf()).orElseThrow(() -> new EntityNotFoundException("Passageiro não encontrado!"));

        if (passageiro.getConfirmacao() != null) {
            throw new RuntimeException("O passageiro já realizou check-in!");
        }

        if (repository.existsPassageiroByConfirmacao_Assento(request.assento().toUpperCase())) {
            throw new EntityExistsException("O assento já está ocupado por outro passageiro!");
        }

        passageiro.setConfirmacao(new Confirmacao(UUID.randomUUID().toString(), request.assento().toUpperCase(), LocalDateTime.now(), request.malasDespachadas()));

        if (passageiro.getClassificacao().equals(Classificacao.VIP)) {
            passageiro.setMilhas(passageiro.getMilhas() + 100);
        } else if (passageiro.getClassificacao().equals(Classificacao.OURO)) {
            passageiro.setMilhas(passageiro.getMilhas() + 80);
        } else if (passageiro.getClassificacao().equals(Classificacao.PRATA)) {
            passageiro.setMilhas(passageiro.getMilhas() + 50);
        } else if (passageiro.getClassificacao().equals(Classificacao.BRONZE)) {
            passageiro.setMilhas(passageiro.getMilhas() + 30);
        } else if (passageiro.getClassificacao().equals(Classificacao.ASSOCIADO)) {
            passageiro.setMilhas(passageiro.getMilhas() + 10);
        }

        List<String> assentos = List.of("1A", "1B", "1C", "1D", "1E", "1F",
                "2A", "2B", "2C", "2D", "2E", "2F",
                "3A", "3B", "3C", "3D", "3E", "3F",
                "4A", "4B", "4C", "4D", "4E", "4F",
                "5A", "5B", "5C", "5D", "5E", "5F",
                "6A", "6B", "6C", "6D", "6E", "6F",
                "7A", "7B", "7C", "7D", "7E", "7F",
                "8A", "8B", "8C", "8D", "8E", "8F",
                "9A", "9B", "9C", "9D", "9E", "9F",
                "10A", "10B", "10C", "10D", "10E", "10F");

        if (!assentos.contains(passageiro.getConfirmacao().getAssento().toUpperCase())) {
            throw new EntityNotFoundException("Assento não encontrado!");
        }

        if ((passageiro.getConfirmacao().getAssento().contains("5") || passageiro.getConfirmacao().getAssento().contains("6")) && LocalDate.now().getYear() - passageiro.getDataNascimento().getYear() < 18) {
            throw new RuntimeException("O passageiro é menor de idade e não pode sentar nas fileiras de emergência (5 e 6)!");
        }

        if ((passageiro.getConfirmacao().getAssento().contains("5") || passageiro.getConfirmacao().getAssento().contains("6")) && !request.malasDespachadas()) {
            throw new RuntimeException("O passageiro deve obrigatoriamente despachar suas malas nas fileiras de emergência (5 e 6)!");
        }

        repository.save(passageiro);
        System.out.println("Confirmação feita pelo passageiro de CPF " + passageiro.getCpf() + " com e-ticket " + passageiro.getConfirmacao().getEticket());
        return new ConfirmacaoResponse(passageiro);
    }

}
