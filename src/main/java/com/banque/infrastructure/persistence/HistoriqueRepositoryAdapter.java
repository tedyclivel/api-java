package com.banque.infrastructure.persistence;

import com.banque.application.port.out.HistoriqueRepository;
import com.banque.domain.model.Historique;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HistoriqueRepositoryAdapter implements HistoriqueRepository {
    private final JpaHistoriqueRepository jpaRepository;

    public HistoriqueRepositoryAdapter(JpaHistoriqueRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Historique sauvegarder(Historique historique) {
        return jpaRepository.save(historique);
    }

    @Override
    public List<Historique> trouverParCompteId(Integer compteId) {
        return jpaRepository.findByCompteIdOrderByDateDesc(compteId);
    }
}
