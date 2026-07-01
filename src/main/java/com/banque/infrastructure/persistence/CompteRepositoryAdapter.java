package com.banque.infrastructure.persistence;

import com.banque.application.port.out.CompteRepository;
import com.banque.domain.model.Compte;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CompteRepositoryAdapter implements CompteRepository {
    private final JpaCompteRepository jpaRepository;

    public CompteRepositoryAdapter(JpaCompteRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Compte sauvegarder(Compte compte) {
        return jpaRepository.save(compte);
    }

    @Override
    public Optional<Compte> trouverParId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Compte> trouverParUtilisateurId(Integer utilisateurId) {
        return jpaRepository.findByUtilisateurId(utilisateurId);
    }

    @Override
    public void mettreAJour(Compte compte) {
        jpaRepository.save(compte);
    }
}
