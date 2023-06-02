package tech.devinhouse.labsky.services;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("Testa os métodos de check-in")
    class confirmacaoCheckIn {
        @Test
        @DisplayName("Quando não existe passageiro com o CPF informado, deve lançar exceção")
        void confirmacao_passageiroNaoEncontrado() {
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
            assertThrows(EntityNotFoundException.class, () -> service.confirmacao(request));
        }

        @Test
        @DisplayName("Quando ocorrer tentativa de check-in com assento já ocupado por outro passageiro, deve lançar exceção")
        void confirmacao_assentoOcupado() {
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
        void confirmacao_assentoInexistente() {
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.VIP, 100);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "15A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            assertThrows(EntityNotFoundException.class, () -> service.confirmacao(request));
        }

        @Test
        @DisplayName("Quando ocorrer tentativa de check-in e o passageiro for menor de idade e tentar sentar em uma das fileiras de emergência (5 e 6), deve lançar exceção")
        void confirmacao_menorIdade() {
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.VIP, 100);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "5A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            assertThrows(RuntimeException.class, () -> service.confirmacao(request));
        }

        @Test
        @DisplayName("Quando ocorrer tentativa de check-in e o passageiro sentar em uma das fileiras de emergência (5 e 6) e não despachar as malas, deve lançar exceção")
        void confirmacao_despacharMalas() {
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.of(2000, 7, 21), Classificacao.VIP, 100);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "5A", false, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            assertThrows(RuntimeException.class, () -> service.confirmacao(request));
        }

        @Test
        @DisplayName("Quando realizar check-in com informações corretas, deve retornar CPF e UUID do passageiro no console")
        void confirmacao() {
            Integer milhas = 100;
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.VIP, milhas);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            var resultado = service.confirmacao(request);
            assertNotNull(resultado);
        }

        @Test
        @DisplayName("Quando realizar check-in com informações corretas e o paciente for VIP, deve incrementar as milhas do passageiro em + 100")
        void confirmacao_passageiroVIP() {
            Integer milhas = 100;
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.VIP, milhas);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            var resultado = service.confirmacao(request);
            assertNotNull(resultado);
            assertEquals(passageiro.getMilhas(), milhas + 100);
        }

        @Test
        @DisplayName("Quando realizar check-in com informações corretas e o paciente for OURO, deve incrementar as milhas do passageiro em + 80")
        void confirmacao_passageiroOURO() {
            Integer milhas = 100;
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.OURO, milhas);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            var resultado = service.confirmacao(request);
            assertNotNull(resultado);
            assertEquals(passageiro.getMilhas(), milhas + 80);
        }

        @Test
        @DisplayName("Quando realizar check-in com informações corretas e o paciente for PRATA, deve incrementar as milhas do passageiro em + 50")
        void confirmacao_passageiroPRATA() {
            Integer milhas = 100;
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.PRATA, milhas);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            var resultado = service.confirmacao(request);
            assertNotNull(resultado);
            assertEquals(passageiro.getMilhas(), milhas + 50);
        }

        @Test
        @DisplayName("Quando realizar check-in com informações corretas e o paciente for BRONZE, deve incrementar as milhas do passageiro em + 30")
        void confirmacao_passageiroBRONZE() {
            Integer milhas = 100;
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.BRONZE, milhas);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            var resultado = service.confirmacao(request);
            assertNotNull(resultado);
            assertEquals(passageiro.getMilhas(), milhas + 30);
        }

        @Test
        @DisplayName("Quando realizar check-in com informações corretas e o paciente for ASSOCIADO, deve incrementar as milhas do passageiro em + 10")
        void confirmacao_passageiroASSOCIADO() {
            Integer milhas = 100;
            Passageiro passageiro = new Passageiro("111.111.111-11", "Halan Germano Bacca", LocalDate.now(), Classificacao.ASSOCIADO, milhas);
            ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", true, "123456", LocalDateTime.now());
            Mockito.when(repository.findById(Mockito.anyString()))
                    .thenReturn(Optional.of(passageiro));
            Mockito.when(repository.existsPassageiroByAssentoIgnoreCase(Mockito.anyString()))
                    .thenReturn(false);
            var resultado = service.confirmacao(request);
            assertNotNull(resultado);
            assertEquals(passageiro.getMilhas(), milhas + 10);
        }
    }

}