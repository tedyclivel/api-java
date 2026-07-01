package com.banque.application.port.in;

import com.banque.domain.model.Compte;
import com.banque.domain.model.Historique;
import com.banque.domain.exception.MetierException;
import java.util.List;

public interface CompteUseCase {
    Compte creerCompte(Integer utilisateurId, String typeCompte) throws MetierException;
    Compte obtenirCompte(Integer compteId) throws MetierException;
    List<Compte> listerComptesUtilisateur(Integer utilisateurId);
    void crediterCompte(Integer compteId, double montant) throws MetierException;
    void debiterCompte(Integer compteId, double montant) throws MetierException;
    void effectuerVirement(Integer compteSourceId, Integer compteDestinationId, double montant) throws MetierException;
    List<Historique> obtenirHistorique(Integer compteId) throws MetierException;
}
