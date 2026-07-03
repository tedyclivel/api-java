package com.banque.application.service;

import com.banque.application.port.out.UtilisateurRepository;
import com.banque.domain.exception.MetierException;
import com.banque.domain.exception.NonTrouveException;
import com.banque.domain.model.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new Utilisateur(1, "Doe", "John", "john@test.com", "encodedPassword", 30, new Date());
    }

    @Test
    void testInscrireUtilisateur_Succes() throws MetierException {
        when(utilisateurRepository.trouverParEmail("john@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(utilisateurRepository.sauvegarder(any(Utilisateur.class))).thenReturn(utilisateur);

        Utilisateur res = utilisateurService.inscrireUtilisateur("Doe", "John", "john@test.com", "password", 30);

        assertNotNull(res);
        assertEquals("John", res.getPrenom());
        verify(utilisateurRepository, times(1)).sauvegarder(any(Utilisateur.class));
    }

    @Test
    void testInscrireUtilisateur_EmailExistant() {
        when(utilisateurRepository.trouverParEmail("john@test.com")).thenReturn(Optional.of(utilisateur));

        assertThrows(MetierException.class, () -> {
            utilisateurService.inscrireUtilisateur("Doe", "John", "john@test.com", "password", 30);
        });

        verify(utilisateurRepository, never()).sauvegarder(any(Utilisateur.class));
    }

    @Test
    void testAuthentifierUtilisateur_Succes() throws MetierException {
        when(utilisateurRepository.trouverParEmail("john@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        Utilisateur res = utilisateurService.authentifierUtilisateur("john@test.com", "password");

        assertNotNull(res);
        assertEquals("john@test.com", res.getEmail());
    }

    @Test
    void testAuthentifierUtilisateur_MauvaisMotDePasse() {
        when(utilisateurRepository.trouverParEmail("john@test.com")).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

        assertThrows(MetierException.class, () -> {
            utilisateurService.authentifierUtilisateur("john@test.com", "wrongpass");
        });
    }

    @Test
    void testAuthentifierUtilisateur_EmailInexistant() {
        when(utilisateurRepository.trouverParEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(MetierException.class, () -> {
            utilisateurService.authentifierUtilisateur("unknown@test.com", "password");
        });
    }

    @Test
    void testObtenirUtilisateur_Succes() throws MetierException {
        when(utilisateurRepository.trouverParId(1)).thenReturn(Optional.of(utilisateur));

        Utilisateur res = utilisateurService.obtenirUtilisateur(1);

        assertNotNull(res);
        assertEquals(1, res.getId());
    }

    @Test
    void testObtenirUtilisateur_NonTrouve() {
        when(utilisateurRepository.trouverParId(99)).thenReturn(Optional.empty());

        assertThrows(NonTrouveException.class, () -> {
            utilisateurService.obtenirUtilisateur(99);
        });
    }
    @Test
    void testListerUtilisateurs() {
        when(utilisateurRepository.trouverTous()).thenReturn(java.util.List.of(utilisateur));
        java.util.List<Utilisateur> list = utilisateurService.listerUtilisateurs();
        assertEquals(1, list.size());
        assertEquals("John", list.get(0).getPrenom());
    }
}
