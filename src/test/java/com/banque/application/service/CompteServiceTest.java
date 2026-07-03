package com.banque.application.service;

import com.banque.application.port.out.CompteRepository;
import com.banque.application.port.out.HistoriqueRepository;
import com.banque.application.port.out.UtilisateurRepository;
import com.banque.domain.exception.MetierException;
import com.banque.domain.exception.NonTrouveException;
import com.banque.domain.exception.SoldeInsuffisantException;
import com.banque.domain.model.Compte;
import com.banque.domain.model.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompteServiceTest {

    @Mock
    private CompteRepository compteRepository;

    @Mock
    private HistoriqueRepository historiqueRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private CompteService compteService;

    private Compte compteSource;
    private Compte compteDest;
    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur(1, "Doe", "John", "john@test.com", "password", 0, new Date());
        compteSource = new Compte(1, 1, "courant", 500.0, new Date());
        compteDest = new Compte(2, 2, "epargne", 100.0, new Date());
    }

    @Test
    void testCreerCompte_Succes() throws MetierException {
        when(utilisateurRepository.trouverParId(1)).thenReturn(Optional.of(utilisateur));
        when(compteRepository.sauvegarder(any(Compte.class))).thenReturn(compteSource);

        Compte res = compteService.creerCompte(1, "courant");

        assertNotNull(res);
        assertEquals("courant", res.getTypeCompte());
        verify(compteRepository, times(1)).sauvegarder(any(Compte.class));
    }

    @Test
    void testCreerCompte_UtilisateurNonTrouve() {
        when(utilisateurRepository.trouverParId(99)).thenReturn(Optional.empty());

        assertThrows(NonTrouveException.class, () -> {
            compteService.creerCompte(99, "courant");
        });
    }

    @Test
    void testObtenirCompte_Succes() throws MetierException {
        when(compteRepository.trouverParId(1)).thenReturn(Optional.of(compteSource));

        Compte res = compteService.obtenirCompte(1);

        assertEquals(1, res.getId());
        assertEquals(500.0, res.getSolde());
    }

    @Test
    void testObtenirCompte_NonTrouve() {
        when(compteRepository.trouverParId(99)).thenReturn(Optional.empty());

        assertThrows(NonTrouveException.class, () -> {
            compteService.obtenirCompte(99);
        });
    }

    @Test
    void testCrediterCompte() throws MetierException {
        when(compteRepository.trouverParId(1)).thenReturn(Optional.of(compteSource));

        compteService.crediterCompte(1, 200.0);

        assertEquals(700.0, compteSource.getSolde());
        verify(compteRepository, times(1)).mettreAJour(compteSource);
        verify(historiqueRepository, times(1)).sauvegarder(any());
    }

    @Test
    void testDebiterCompte_Succes() throws MetierException {
        when(compteRepository.trouverParId(1)).thenReturn(Optional.of(compteSource));

        compteService.debiterCompte(1, 200.0);

        assertEquals(300.0, compteSource.getSolde());
        verify(compteRepository, times(1)).mettreAJour(compteSource);
        verify(historiqueRepository, times(1)).sauvegarder(any());
    }

    @Test
    void testDebiterCompte_SoldeInsuffisant() {
        when(compteRepository.trouverParId(1)).thenReturn(Optional.of(compteSource));

        assertThrows(SoldeInsuffisantException.class, () -> {
            compteService.debiterCompte(1, 600.0);
        });
        
        verify(compteRepository, never()).mettreAJour(any());
        verify(historiqueRepository, never()).sauvegarder(any());
    }

    @Test
    void testEffectuerVirement_Succes() throws MetierException {
        when(compteRepository.trouverParId(1)).thenReturn(Optional.of(compteSource));
        when(compteRepository.trouverParId(2)).thenReturn(Optional.of(compteDest));

        compteService.effectuerVirement(1, 2, 200.0);

        assertEquals(300.0, compteSource.getSolde());
        assertEquals(300.0, compteDest.getSolde());
        verify(compteRepository, times(2)).mettreAJour(any());
        verify(historiqueRepository, times(2)).sauvegarder(any());
    }

    @Test
    void testEffectuerVirement_MemeCompte() {
        assertThrows(MetierException.class, () -> {
            compteService.effectuerVirement(1, 1, 100.0);
        });
    }
}
