package com.banque.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilisateurTest {

    @Test
    void testGettersAndSetters() {
        Date now = new Date();
        Utilisateur u = new Utilisateur();

        u.setId(1);
        u.setNom("Dupont");
        u.setPrenom("Jean");
        u.setEmail("jean@test.com");
        u.setMotDePasse("pass");
        u.setAge(30);
        u.setDateCreation(now);

        assertEquals(1, u.getId());
        assertEquals("Dupont", u.getNom());
        assertEquals("Jean", u.getPrenom());
        assertEquals("jean@test.com", u.getEmail());
        assertEquals("pass", u.getMotDePasse());
        assertEquals(30, u.getAge());
        assertEquals(now, u.getDateCreation());
    }

    @Test
    void testConstructors() {
        Date now = new Date();
        Utilisateur u = new Utilisateur(1, "Dupont", "Jean", "jean@test.com", "pass", 30, now);
        assertEquals(1, u.getId());
        assertEquals("Dupont", u.getNom());
        assertEquals("Jean", u.getPrenom());
        assertEquals("jean@test.com", u.getEmail());
        assertEquals("pass", u.getMotDePasse());
        assertEquals(30, u.getAge());
        assertEquals(now, u.getDateCreation());
    }
}
