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
}
