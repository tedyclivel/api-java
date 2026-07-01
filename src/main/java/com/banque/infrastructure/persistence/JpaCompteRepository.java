package com.banque.infrastructure.persistence;

import com.banque.domain.model.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaCompteRepository extends JpaRepository<Compte, Integer> {
    List<Compte> findByUtilisateurId(Integer utilisateurId);
}
