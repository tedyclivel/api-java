package com.banque.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HistoriqueTest {

    @Test
    void testGettersAndSetters() {
        Date now = new Date();
        Historique h = new Historique();
        
        h.setId(1);
        h.setCompteId(2);
        h.setAutrePartieId(3);
        h.setType("depot");
        h.setMontant(100.0);
        h.setDate(now);

        assertEquals(1, h.getId());
        assertEquals(2, h.getCompteId());
        assertEquals(3, h.getAutrePartieId());
        assertEquals("depot", h.getType());
        assertEquals(100.0, h.getMontant(), 0.001);
        assertEquals(now, h.getDate());
    }

    @Test
    void testConstructors() {
        Date now = new Date();
        Historique h1 = new Historique(1, 2, "retrait", 50.0, now, 3);
        assertEquals(1, h1.getId());
        assertEquals(2, h1.getCompteId());
        assertEquals("retrait", h1.getType());
        assertEquals(50.0, h1.getMontant(), 0.001);
        assertEquals(now, h1.getDate());
        assertEquals(3, h1.getAutrePartieId());

        Historique h2 = new Historique(2, null, "depot", 100.0, now, null);
        assertEquals(2, h2.getId());
        assertNull(h2.getCompteId());
        assertEquals("depot", h2.getType());
        assertEquals(100.0, h2.getMontant(), 0.001);
        assertEquals(now, h2.getDate());
        assertNull(h2.getAutrePartieId());
    }
}
