package com.banque.application.service;

import com.banque.application.port.in.CompteUseCase;
import com.banque.application.port.out.CompteRepository;
import com.banque.application.port.out.HistoriqueRepository;
import com.banque.application.port.out.UtilisateurRepository;
import com.banque.domain.exception.MetierException;
import com.banque.domain.exception.NonTrouveException;
import com.banque.domain.model.Compte;
import com.banque.domain.model.Historique;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CompteService implements CompteUseCase {
    
    private final CompteRepository compteRepository;
    private final HistoriqueRepository historiqueRepository;
    private final UtilisateurRepository utilisateurRepository;

    public CompteService(CompteRepository compteRepository, HistoriqueRepository historiqueRepository, UtilisateurRepository utilisateurRepository) {
        this.compteRepository = compteRepository;
        this.historiqueRepository = historiqueRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Compte creerCompte(Integer utilisateurId, String typeCompte) throws MetierException {
        if (utilisateurRepository.trouverParId(utilisateurId).isEmpty()) {
            throw new NonTrouveException("Utilisateur introuvable pour la création du compte.");
        }
        
        Compte nouveauCompte = new Compte(null, utilisateurId, typeCompte, 0.0, new Date());
        return compteRepository.sauvegarder(nouveauCompte);
    }

    @Override
    public Compte obtenirCompte(Integer compteId) throws MetierException {
        return compteRepository.trouverParId(compteId)
                .orElseThrow(() -> new NonTrouveException("Compte non trouvé avec l'id : " + compteId));
    }

    @Override
    public List<Compte> listerComptesUtilisateur(Integer utilisateurId) {
        return compteRepository.trouverParUtilisateurId(utilisateurId);
    }

    @Override
    public void crediterCompte(Integer compteId, double montant) throws MetierException {
        Compte compte = obtenirCompte(compteId);
        compte.crediter(montant);
        compteRepository.mettreAJour(compte);
        
        Historique hist = new Historique(null, compteId, "DEPOT", montant, new Date(), null);
        historiqueRepository.sauvegarder(hist);
    }

    @Override
    public void debiterCompte(Integer compteId, double montant) throws MetierException {
        Compte compte = obtenirCompte(compteId);
        compte.debiter(montant); // This throws SoldeInsuffisantException if needed
        compteRepository.mettreAJour(compte);
        
        Historique hist = new Historique(null, compteId, "RETRAIT", montant, new Date(), null);
        historiqueRepository.sauvegarder(hist);
    }

    @Override
    public void effectuerVirement(Integer compteSourceId, Integer compteDestinationId, double montant) throws MetierException {
        if (compteSourceId.equals(compteDestinationId)) {
            throw new MetierException("Le compte source et destination doivent être différents.");
        }
        
        Compte source = obtenirCompte(compteSourceId);
        Compte destination = obtenirCompte(compteDestinationId);
        
        source.debiter(montant);
        destination.crediter(montant);
        
        compteRepository.mettreAJour(source);
        compteRepository.mettreAJour(destination);
        
        Historique histSource = new Historique(null, compteSourceId, "VIREMENT_ENVOYE", montant, new Date(), compteDestinationId);
        historiqueRepository.sauvegarder(histSource);
        
        Historique histDest = new Historique(null, compteDestinationId, "VIREMENT_RECU", montant, new Date(), compteSourceId);
        historiqueRepository.sauvegarder(histDest);
    }

    @Override
    public List<Historique> obtenirHistorique(Integer compteId) throws MetierException {
        obtenirCompte(compteId); // Verify compte exists
        return historiqueRepository.trouverParCompteId(compteId);
    }
}
