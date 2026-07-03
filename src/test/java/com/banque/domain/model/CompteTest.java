package com.banque.domain.model;

import com.banque.domain.exception.SoldeInsuffisantException;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CompteTest {

    @Test
    void testCrediterCompte() {
        Compte compte = new Compte(1, 1, "courant", 100.0, new Date());
        compte.crediter(50.0);
        assertEquals(150.0, compte.getSolde(), 0.001);
    }

    @Test
    void testDebiterCompteSucces() throws SoldeInsuffisantException {
        Compte compte = new Compte(1, 1, "courant", 100.0, new Date());
        compte.debiter(50.0);
        assertEquals(50.0, compte.getSolde(), 0.001);
    }

    @Test
    void testDebiterCompteSoldeInsuffisant() {
        Compte compte = new Compte(1, 1, "courant", 100.0, new Date());
        assertThrows(SoldeInsuffisantException.class, () -> {
            compte.debiter(150.0);
        });
    }
    @Test
    void testGettersAndSetters() {
        Date now = new Date();
        Compte c = new Compte();
        
        c.setId(10);
        c.setUtilisateurId(20);
        c.setTypeCompte("epargne");
        c.setSolde(1000.0);
        c.setDateCreation(now);

        assertEquals(10, c.getId());
        assertEquals(20, c.getUtilisateurId());
        assertEquals("epargne", c.getTypeCompte());
        assertEquals(1000.0, c.getSolde(), 0.001);
        assertEquals(now, c.getDateCreation());
    }
}
