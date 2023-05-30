package tech.devinhouse.labsky.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.devinhouse.labsky.models.Passageiro;
import tech.devinhouse.labsky.repositories.PassageiroRepository;

import java.util.List;

@Service
public class PassageiroService {

    @Autowired
    private PassageiroRepository repository;

    public List<Passageiro> listaPassageiros() {
        return repository.findAll();
    }

    public Passageiro listaPassageiroPeloCpf(String cpf) {
        return repository.findById(cpf).orElseThrow(EntityNotFoundException::new);
    }

}
