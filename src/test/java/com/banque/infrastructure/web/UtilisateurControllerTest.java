package com.banque.infrastructure.web;

import com.banque.application.port.out.UtilisateurRepository;
import com.banque.domain.model.Utilisateur;
import com.banque.infrastructure.security.JwtService;
import com.banque.infrastructure.security.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Clear db before each test
        // This is a simple integration test, assume the H2 DB is fresh.
    }

    @Test
    void testRegisterAndLogin() throws Exception {
        // Register
        Map<String, Object> registerPayload = Map.of(
                "nom", "Dupont",
                "prenom", "Jean",
                "email", "jean.dupont@test.com",
                "motDePasse", "password123",
                "age", 30
        );

        mockMvc.perform(post("/utilisateurs/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Utilisateur créé avec succès"));

        // Login
        Map<String, String> loginPayload = Map.of(
                "email", "jean.dupont@test.com",
                "motDePasse", "password123"
        );

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLogin_BadCredentials() throws Exception {
        Map<String, String> loginPayload = Map.of(
                "email", "nonexistent@test.com",
                "motDePasse", "wrong"
        );

        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Doe");
        request.setPrenom("John");
        request.setEmail("john.doe2@test.com");
        request.setMotDePasse("password123");
        request.setAge(30);

        mockMvc.perform(post("/utilisateurs/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/utilisateurs/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Un utilisateur avec cet email existe déjà."));
    }

    @Test
    void testDtoSettersAndGetters() {
        RegisterRequest r = new RegisterRequest();
        r.setNom("n");
        r.setPrenom("p");
        r.setEmail("e");
        r.setMotDePasse("m");
        r.setAge(10);
        org.junit.jupiter.api.Assertions.assertEquals("n", r.getNom());
        org.junit.jupiter.api.Assertions.assertEquals("p", r.getPrenom());
        org.junit.jupiter.api.Assertions.assertEquals("e", r.getEmail());
        org.junit.jupiter.api.Assertions.assertEquals("m", r.getMotDePasse());
        org.junit.jupiter.api.Assertions.assertEquals(10, r.getAge());

        LoginRequest l = new LoginRequest();
        l.setEmail("e2");
        l.setMotDePasse("m2");
        org.junit.jupiter.api.Assertions.assertEquals("e2", l.getEmail());
        org.junit.jupiter.api.Assertions.assertEquals("m2", l.getMotDePasse());
    }
}
