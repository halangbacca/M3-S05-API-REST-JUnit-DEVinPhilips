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
@RequestMapping("/api/passageiros")
public class PassageiroController {

    @Autowired
    private PassageiroService service;

    @GetMapping
    public List<Passageiro> listaPassageiros() {
        return service.listaPassageiros();
    }
    
}
