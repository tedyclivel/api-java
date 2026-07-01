package com.banque.application.port.out;

import com.banque.domain.model.Utilisateur;
import java.util.Optional;
import java.util.List;

public interface UtilisateurRepository {
    Utilisateur sauvegarder(Utilisateur utilisateur);
    Optional<Utilisateur> trouverParId(Integer id);
    Optional<Utilisateur> trouverParEmail(String email);
    List<Utilisateur> trouverTous();
    void supprimer(Integer id);
}
