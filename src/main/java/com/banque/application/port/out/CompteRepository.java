package com.banque.application.port.out;

import com.banque.domain.model.Compte;
import java.util.Optional;
import java.util.List;

public interface CompteRepository {
    Compte sauvegarder(Compte compte);
    Optional<Compte> trouverParId(Integer id);
    List<Compte> trouverParUtilisateurId(Integer utilisateurId);
    void mettreAJour(Compte compte);
}
