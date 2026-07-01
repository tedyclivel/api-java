package com.banque.infrastructure.web;

import com.banque.application.port.in.CompteUseCase;
import com.banque.domain.exception.MetierException;
import com.banque.domain.model.Compte;
import com.banque.domain.model.Historique;
import com.banque.infrastructure.persistence.JpaUtilisateurRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class CompteController {

    private final CompteUseCase compteUseCase;
    private final JpaUtilisateurRepository utilisateurRepository;

    public CompteController(CompteUseCase compteUseCase, JpaUtilisateurRepository utilisateurRepository) {
        this.compteUseCase = compteUseCase;
        this.utilisateurRepository = utilisateurRepository;
    }

    private Integer getUtilisateurConnecteId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return utilisateurRepository.findByEmail(email).orElseThrow().getId();
        }
        return null;
    }

    @GetMapping("/mes-comptes")
    public ResponseEntity<?> getMesComptes() {
        Integer uId = getUtilisateurConnecteId();
        List<Compte> comptes = compteUseCase.listerComptesUtilisateur(uId);
        return ResponseEntity.ok(comptes);
    }

    @PostMapping("/comptes")
    public ResponseEntity<?> creerCompte(@RequestBody Map<String, Object> body) {
        try {
            Integer uId = getUtilisateurConnecteId();
            String typeCompte = (String) body.getOrDefault("typeCompte", "epargne");
            Compte c = compteUseCase.creerCompte(uId, typeCompte);
            return ResponseEntity.status(201).body(c);
        } catch (MetierException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/comptes/{id}")
    public ResponseEntity<?> getDetailsCompte(@PathVariable Integer id) {
        try {
            Compte c = compteUseCase.obtenirCompte(id);
            if (!c.getUtilisateurId().equals(getUtilisateurConnecteId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Accès refusé."));
            }
            return ResponseEntity.ok(c);
        } catch (MetierException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/comptes/{id}/historique")
    public ResponseEntity<?> getHistorique(@PathVariable Integer id) {
        try {
            Compte c = compteUseCase.obtenirCompte(id);
            if (!c.getUtilisateurId().equals(getUtilisateurConnecteId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Accès refusé."));
            }
            List<Historique> hist = compteUseCase.obtenirHistorique(id);
            return ResponseEntity.ok(hist);
        } catch (MetierException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/comptes/{id}/depot")
    public ResponseEntity<?> depot(@PathVariable Integer id, @RequestBody Map<String, Double> body) {
        try {
            Compte c = compteUseCase.obtenirCompte(id);
            if (!c.getUtilisateurId().equals(getUtilisateurConnecteId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Accès refusé."));
            }
            compteUseCase.crediterCompte(id, body.get("montant"));
            return ResponseEntity.ok(Map.of("message", "Dépôt réussi."));
        } catch (MetierException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/comptes/{id}/retrait")
    public ResponseEntity<?> retrait(@PathVariable Integer id, @RequestBody Map<String, Double> body) {
        try {
            Compte c = compteUseCase.obtenirCompte(id);
            if (!c.getUtilisateurId().equals(getUtilisateurConnecteId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Accès refusé."));
            }
            compteUseCase.debiterCompte(id, body.get("montant"));
            return ResponseEntity.ok(Map.of("message", "Retrait réussi."));
        } catch (MetierException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/comptes/{id}/virement")
    public ResponseEntity<?> virement(@PathVariable Integer id, @RequestBody Map<String, Object> body) {
        try {
            Compte c = compteUseCase.obtenirCompte(id);
            if (!c.getUtilisateurId().equals(getUtilisateurConnecteId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Accès refusé."));
            }
            Double montant = Double.parseDouble(body.get("montant").toString());
            Integer destinataireId = Integer.parseInt(body.get("destinataireId").toString());
            compteUseCase.effectuerVirement(id, destinataireId, montant);
            return ResponseEntity.ok(Map.of("message", "Virement réussi."));
        } catch (MetierException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
