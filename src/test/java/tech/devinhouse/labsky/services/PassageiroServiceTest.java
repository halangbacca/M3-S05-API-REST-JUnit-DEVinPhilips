package tech.devinhouse.labsky.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.devinhouse.labsky.enums.Classificacao;
import tech.devinhouse.labsky.models.Passageiro;
import tech.devinhouse.labsky.records.request.ConfirmacaoRequest;
import tech.devinhouse.labsky.records.response.ConsultaCPFResponse;
import tech.devinhouse.labsky.repositories.PassageiroRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PassageiroServiceTest {

    @Mock
    private PassageiroRepository repository;

    @InjectMocks
    private PassageiroService service;

    @Test
    @DisplayName("Quando há registros de passageiros, deve retornar lista com valores")
    void listaPassageiros() {
        List<Passageiro> passageiros = List.of(
                new Passageiro("111.111.111-11", "Halan Germano Bacca"),
                new Passageiro("222.222.222-22", "Luciana Lamim")
        );
        Mockito.when(repository.findAll()).thenReturn(passageiros);

        List<Passageiro> lista = service.listaPassageiros();

        assertNotNull(lista);
        assertFalse(lista.isEmpty());
        assertEquals(passageiros.size(), lista.size());
    }

    @Test
    @DisplayName("Quando não há registros de passageiros, deve retornar uma lista vazia")
    void listaPassageiros_semRegistros() {
        List<Passageiro> lista = service.listaPassageiros();
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("Quando existe passageiro com o CPF informado, deve retornar o passageiro")
    void listaPassageiroPeloCPF() {
        String cpf = "111.111.111-11";
        Passageiro passageiro = new Passageiro(cpf, "Halan Germano Bacca");
        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(passageiro));
        ConsultaCPFResponse resultado = service.listaPassageiroPeloCpf(passageiro.getCpf());
        assertNotNull(resultado);
        assertEquals(cpf, resultado.cpf());
    }

    @Test
    @DisplayName("Quando não existe passageiro com o CPF informado, deve lançar exceção")
    void listaPassageiroPeloCPF_naoEncontrado() {
        String cpf = "111.111.111-11";
        assertThrows(EntityNotFoundException.class, () -> service.listaPassageiroPeloCpf(cpf));
    }

    @Test
    @DisplayName("Quando não existe passageiro com o CPF informado, deve lançar exceção")
    void confirmacao_PassageiroNaoEncontrado() {
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
        assertThrows(EntityNotFoundException.class, () -> service.confirmacao(request));
    }

    @Test
    @DisplayName("Quando ocorrer tentativa de check-in com assento já ocupado por outro passageiro, deve lançar exceção")
    void confirmacao_AssentoOcupado() {
        Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca");
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(passageiro));
        Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                .thenReturn(true);
        assertThrows(EntityExistsException.class, () -> service.confirmacao(request));
    }

    @Test
    @DisplayName("Quando ocorrer tentativa de check-in e não for possível encontrar o assento, deve lançar exceção")
    void confirmacao_AssentoInexistente() {
        Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.VIP, 100);
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "15A", true, "123456", LocalDateTime.now());
        Mockito.when(repository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(passageiro));
        Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                .thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> service.confirmacao(request));
    }


}