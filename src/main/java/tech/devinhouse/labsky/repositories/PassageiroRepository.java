package tech.devinhouse.labsky.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.devinhouse.labsky.models.Passageiro;

@Repository
public interface PassageiroRepository extends JpaRepository<Passageiro, String> {
    boolean existsPassageiroByConfirmacao_Assento(String assento);
}
