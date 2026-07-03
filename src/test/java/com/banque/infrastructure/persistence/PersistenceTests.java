package com.banque.infrastructure.persistence;

import com.banque.domain.model.Utilisateur;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class PersistenceTests {

    @Autowired
    private UtilisateurRepositoryAdapter utilisateurRepositoryAdapter;

    @Test
    void testUtilisateurRepositoryAdapter() {
        Utilisateur u = new Utilisateur(null, "Test", "Test", "test.pers@test.com", "pass", 20, new Date());
        Utilisateur saved = utilisateurRepositoryAdapter.sauvegarder(u);

        Optional<Utilisateur> found = utilisateurRepositoryAdapter.trouverParId(saved.getId());
        assertTrue(found.isPresent());

        List<Utilisateur> tous = utilisateurRepositoryAdapter.trouverTous();
        assertTrue(tous.size() > 0);
    }
}
