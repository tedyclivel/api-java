package com.banque.application.service;

import com.banque.application.port.in.UtilisateurUseCase;
import com.banque.application.port.out.UtilisateurRepository;
import com.banque.domain.exception.MetierException;
import com.banque.domain.exception.NonTrouveException;
import com.banque.domain.model.Utilisateur;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UtilisateurService implements UtilisateurUseCase {
    
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Utilisateur inscrireUtilisateur(String nom, String prenom, String email, String motDePasse, int age) throws MetierException {
        if (utilisateurRepository.trouverParEmail(email).isPresent()) {
            throw new MetierException("Un utilisateur avec cet email existe déjà.");
        }
        
        String hashMdp = passwordEncoder.encode(motDePasse);
        Utilisateur nouvelUtilisateur = new Utilisateur(null, nom, prenom, email, hashMdp, age, new Date());
        
        return utilisateurRepository.sauvegarder(nouvelUtilisateur);
    }

    @Override
    public Utilisateur authentifierUtilisateur(String email, String motDePasse) throws MetierException {
        Utilisateur user = utilisateurRepository.trouverParEmail(email)
                .orElseThrow(() -> new MetierException("Email ou mot de passe incorrect."));
                
        if (!passwordEncoder.matches(motDePasse, user.getMotDePasse())) {
            throw new MetierException("Email ou mot de passe incorrect.");
        }
        
        return user;
    }

    @Override
    public Utilisateur obtenirUtilisateur(Integer id) throws MetierException {
        return utilisateurRepository.trouverParId(id)
                .orElseThrow(() -> new NonTrouveException("Utilisateur non trouvé avec l'id : " + id));
    }

    @Override
    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.trouverTous();
    }
}
