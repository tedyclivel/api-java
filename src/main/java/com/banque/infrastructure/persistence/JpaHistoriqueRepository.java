package com.banque.infrastructure.persistence;

import com.banque.domain.model.Historique;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaHistoriqueRepository extends JpaRepository<Historique, Integer> {
    List<Historique> findByCompteIdOrderByDateDesc(Integer compteId);
}
