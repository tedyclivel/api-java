package com.banque.application.port.in;

import com.banque.domain.model.Utilisateur;
import com.banque.domain.exception.MetierException;
import java.util.List;

public interface UtilisateurUseCase {
    Utilisateur inscrireUtilisateur(String nom, String prenom, String email, String motDePasse, int age) throws MetierException;
    Utilisateur authentifierUtilisateur(String email, String motDePasse) throws MetierException;
    Utilisateur obtenirUtilisateur(Integer id) throws MetierException;
    List<Utilisateur> listerUtilisateurs();
}
