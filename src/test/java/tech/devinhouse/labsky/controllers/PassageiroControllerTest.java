package tech.devinhouse.labsky.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import tech.devinhouse.labsky.enums.Classificacao;
import tech.devinhouse.labsky.models.Confirmacao;
import tech.devinhouse.labsky.models.Passageiro;
import tech.devinhouse.labsky.records.request.ConfirmacaoRequest;
import tech.devinhouse.labsky.records.response.ConfirmacaoResponse;
import tech.devinhouse.labsky.records.response.ConsultaCPFResponse;
import tech.devinhouse.labsky.services.PassageiroService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class PassageiroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PassageiroService service;

    @Test
    @DisplayName("Quando há passageiros cadastrados, deve retornar uma lista de passageiros")
    void listaPassageiros() throws Exception {
        var passageiros = List.of(
                new Passageiro("111.111.111-11", "Phoebe Buffay"),
                new Passageiro("222.222.222-22", "Ross Geller")
        );
        Mockito.when(service.listaPassageiros()).thenReturn(passageiros);
        mockMvc.perform(get("/api/passageiros")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].cpf", is(passageiros.get(0).getCpf())))
                .andExpect(jsonPath("$[1].cpf", is(passageiros.get(1).getCpf())));
    }

    @Test
    @DisplayName("Quando não há passageiros cadastrados, deve retornar uma lista vazia")
    void listaPassageiros_vazio() throws Exception {
        mockMvc.perform(get("/api/passageiros")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    @DisplayName("Quando há um passageiro cadastrado com o CPF informado, deve retornar o passageiro")
    void listaPassageiroPeloCPF() throws Exception {
        var passageiro = new ConsultaCPFResponse("111.111.111-11", "Phoebe Buffay", LocalDate.of(1998, 7, 21), Classificacao.VIP, 100);
        Mockito.when(service.listaPassageiroPeloCpf(Mockito.anyString())).thenReturn(passageiro);
        mockMvc.perform(get("/api/passageiros/{cpf}", "111.111.111-11")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$.cpf", is(passageiro.cpf())));
    }

    @Test
    @DisplayName("Quando não há passageiro cadastrado com o CPF informado, deve lançar uma exceção")
    void listaPassageiroPeloCPF_naoEncontrado() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("Passageiro")).when(service).listaPassageiroPeloCpf(Mockito.anyString());
        mockMvc.perform(get("/api/passageiros/{cpf}", "111.111.111-11")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Quando há assentos cadastrados, deve retornar uma lista de assentos")
    void listaAssentos() throws Exception {
        mockMvc.perform(get("/api/assentos")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk()) // 200
                .andExpect(jsonPath("$", hasSize(60)));
    }

    @Test
    @DisplayName("Quando realiza o check-in com dados válidos, deve retornar sucesso")
    void checkin_valido() throws Exception {
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", false, "123456", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.confirmacao(Mockito.any(ConfirmacaoRequest.class))).thenReturn(new ConfirmacaoResponse(new Confirmacao(request.eticket(), request.assento(), request.dataHoraConfirmacao(), request.malasDespachadas())));
        mockMvc.perform(post("/api/passageiros/confirmacao")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())  // 200
                .andExpect(jsonPath("$.eticket", is(request.eticket())))
                .andExpect(jsonPath("$.dataHoraConfirmacao", is(request.dataHoraConfirmacao().toString())));
    }

    @Test
    @DisplayName("Quando realiza o check-in com passageiro inexistente, deve lançar exceção")
    void checkin_invalidoCPFInexistente() throws Exception {
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", false, "123456", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.confirmacao(Mockito.any(ConfirmacaoRequest.class))).thenThrow(new EntityNotFoundException("Passageiro não encontrado!"));
        mockMvc.perform(post("/api/passageiros/confirmacao")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Quando realiza o check-in com assento inexistente, deve lançar exceção")
    void checkin_invalidoAssentoInexistente() throws Exception {
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", false, "123456", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.confirmacao(Mockito.any(ConfirmacaoRequest.class))).thenThrow(new EntityNotFoundException("Assento não encontrado!"));
        mockMvc.perform(post("/api/passageiros/confirmacao")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound()); // 404
    }

    @Test
    @DisplayName("Quando realiza o check-in com assento já ocupado por outro passageiro, deve lançar exceção")
    void checkin_invalidoAssentoOcupado() throws Exception {
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "1A", false, "123456", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.confirmacao(Mockito.any(ConfirmacaoRequest.class))).thenThrow(new EntityExistsException("O assento já está ocupado por outro passageiro!"));
        mockMvc.perform(post("/api/passageiros/confirmacao")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict()); // 409
    }

    @Test
    @DisplayName("Quando passageiro menor de idade seleciona assentos de emergência, deve lançar exceção")
    void checkin_invalidoMenorIdade() throws Exception {
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "5A", false, "123456", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.confirmacao(Mockito.any(ConfirmacaoRequest.class))).thenThrow(new RuntimeException("O passageiro é menor de idade e não pode sentar nas fileiras de emergência (5 e 6)"));
        mockMvc.perform(post("/api/passageiros/confirmacao")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 404
    }

    @Test
    @DisplayName("Quando passageiro seleciona assento de emergência e não despacha as malas, deve lançar exceção")
    void checkin_invalidoDespacharMalas() throws Exception {
        ConfirmacaoRequest request = new ConfirmacaoRequest("111.111.111-11", "5A", false, "123456", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(request);
        Mockito.when(service.confirmacao(Mockito.any(ConfirmacaoRequest.class))).thenThrow(new RuntimeException("O passageiro deve obrigatoriamente despachar suas malas nas fileiras de emergência (5 e 6)"));
        mockMvc.perform(post("/api/passageiros/confirmacao")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // 404
    }

    @Test
    @DisplayName("Quando realiza o check-in com dados inválidos, deve retornar erros")
    void checkin_invalido() throws Exception {
        ConfirmacaoRequest req = new ConfirmacaoRequest("", "", false, "", LocalDateTime.now());
        String requestJson = objectMapper.writeValueAsString(req);
        mockMvc.perform(post("/api/passageiros/confirmacao")
                        .content(requestJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())  // 400
                .andExpect(jsonPath("$[0].mensagem", containsStringIgnoringCase("must not be blank")))
                .andExpect(jsonPath("$[1].mensagem", containsStringIgnoringCase("must not be blank")));
    }

}