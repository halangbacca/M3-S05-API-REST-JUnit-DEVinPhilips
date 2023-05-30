package tech.devinhouse.labsky.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.devinhouse.labsky.models.Passageiro;
import tech.devinhouse.labsky.services.PassageiroService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PassageiroController {

    @Autowired
    private PassageiroService service;

    @GetMapping("/passageiros")
    public List<Passageiro> listaPassageiros() {
        return service.listaPassageiros();
    }

    @GetMapping({"/passageiros/{cpf}"})
    public Passageiro listaPassageiroPeloCpf(@PathVariable String cpf) {
        return service.listaPassageiroPeloCpf(cpf);
    }

    @GetMapping("/assentos")
    public List<String> listaAssentos() {
        return List.of("1A", "1B", "1C", "1D", "1E", "1F",
                "2A", "2B", "2C", "2D", "2E", "2F",
                "3A", "3B", "3C", "3D", "3E", "3F",
                "4A", "4B", "4C", "4D", "4E", "4F",
                "5A", "5B", "5C", "5D", "5E", "5F",
                "6A", "6B", "6C", "6D", "6E", "6F",
                "7A", "7B", "7C", "7D", "7E", "7F",
                "8A", "8B", "8C", "8D", "8E", "8F",
                "9A", "9B", "9C", "9D", "9E", "9F",
                "10A", "10B", "10C", "10D", "10E", "10F");
    }

}
