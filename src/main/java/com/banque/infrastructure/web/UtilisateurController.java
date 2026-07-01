package com.banque.infrastructure.web;

import com.banque.application.port.in.CompteUseCase;
import com.banque.application.port.in.UtilisateurUseCase;
import com.banque.domain.exception.MetierException;
import com.banque.domain.model.Compte;
import com.banque.domain.model.Utilisateur;
import com.banque.infrastructure.security.CustomUserDetailsService;
import com.banque.infrastructure.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class UtilisateurController {

    private final UtilisateurUseCase utilisateurUseCase;
    private final CompteUseCase compteUseCase;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public UtilisateurController(UtilisateurUseCase utilisateurUseCase, CompteUseCase compteUseCase, JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.utilisateurUseCase = utilisateurUseCase;
        this.compteUseCase = compteUseCase;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/utilisateurs/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            Utilisateur user = utilisateurUseCase.inscrireUtilisateur(
                    request.getNom(), request.getPrenom(), request.getEmail(), request.getMotDePasse(), request.getAge());
            
            Compte compte = compteUseCase.creerCompte(user.getId(), "courant");

            if (request.getSoldeInitial() != null && request.getSoldeInitial() > 0) {
               compteUseCase.crediterCompte(compte.getId(), request.getSoldeInitial());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Utilisateur créé avec succès", "utilisateurId", user.getId()));
        } catch (MetierException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            utilisateurUseCase.authentifierUtilisateur(request.getEmail(), request.getMotDePasse());
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (MetierException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }
}

class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private int age;
    private Double soldeInitial;
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public Double getSoldeInitial() { return soldeInitial; }
    public void setSoldeInitial(Double soldeInitial) { this.soldeInitial = soldeInitial; }
}

class LoginRequest {
    private String email;
    private String motDePasse;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
}
