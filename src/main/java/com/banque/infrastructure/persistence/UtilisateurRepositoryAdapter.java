package com.banque.infrastructure.persistence;

import com.banque.application.port.out.UtilisateurRepository;
import com.banque.domain.model.Utilisateur;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UtilisateurRepositoryAdapter implements UtilisateurRepository {
    private final JpaUtilisateurRepository jpaRepository;

    public UtilisateurRepositoryAdapter(JpaUtilisateurRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Utilisateur sauvegarder(Utilisateur utilisateur) {
        return jpaRepository.save(utilisateur);
    }

    @Override
    public Optional<Utilisateur> trouverParId(Integer id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Utilisateur> trouverParEmail(String email) {
        return jpaRepository.findByEmail(email);
    }

    @Override
    public List<Utilisateur> trouverTous() {
        return jpaRepository.findAll();
    }

    @Override
    public void supprimer(Integer id) {
        jpaRepository.deleteById(id);
    }
}
