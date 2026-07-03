package com.banque.infrastructure.web;

import com.banque.application.port.out.CompteRepository;
import com.banque.application.port.out.UtilisateurRepository;
import com.banque.domain.model.Compte;
import com.banque.domain.model.Utilisateur;
import com.banque.infrastructure.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CompteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private String validToken;
    private Utilisateur testUser;

    @BeforeEach
    void setUp() {
        if (utilisateurRepository.trouverParEmail("test@banque.com").isEmpty()) {
            testUser = new Utilisateur(null, "Test", "User", "test@banque.com", passwordEncoder.encode("pass"), 25, new Date());
            testUser = utilisateurRepository.sauvegarder(testUser);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername("test@banque.com");
            validToken = jwtService.generateToken(userDetails);
        } else {
            testUser = utilisateurRepository.trouverParEmail("test@banque.com").get();
            UserDetails userDetails = userDetailsService.loadUserByUsername("test@banque.com");
            validToken = jwtService.generateToken(userDetails);
        }
    }

    @Test
    void testGetMesComptes_Unauthorized() throws Exception {
        mockMvc.perform(get("/mes-comptes"))
                .andExpect(status().isForbidden()); // Spring Security default without auth entry point
    }

    @Test
    void testCreateAndGetComptes() throws Exception {
        // Create account
        Map<String, String> payload = Map.of("typeCompte", "courant");

        mockMvc.perform(post("/comptes")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.typeCompte").value("courant"));

        // Get accounts
        mockMvc.perform(get("/mes-comptes")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].typeCompte").value("courant"));
    }

    @Test
    void testOperationsCompte() throws Exception {
        // 1. Créer un compte source
        Map<String, String> payload = Map.of("typeCompte", "courant");
        String responseSource = mockMvc.perform(post("/comptes")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        Integer sourceId = com.jayway.jsonpath.JsonPath.read(responseSource, "$.id");

        // 2. Depot
        Map<String, Double> depotPayload = Map.of("montant", 500.0);
        mockMvc.perform(post("/comptes/" + sourceId + "/depot")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depotPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Dépôt réussi."));

        // 3. Retrait
        Map<String, Double> retraitPayload = Map.of("montant", 100.0);
        mockMvc.perform(post("/comptes/" + sourceId + "/retrait")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(retraitPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Retrait réussi."));

        // 4. Details du compte
        mockMvc.perform(get("/comptes/" + sourceId)
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.solde").value(400.0));

        // 5. Créer un compte destinataire
        String responseDest = mockMvc.perform(post("/comptes")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("typeCompte", "epargne"))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        Integer destId = com.jayway.jsonpath.JsonPath.read(responseDest, "$.id");

        // 6. Virement
        Map<String, Object> virementPayload = Map.of(
                "montant", 150.0,
                "destinataireId", destId
        );
        mockMvc.perform(post("/comptes/" + sourceId + "/virement")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(virementPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Virement réussi."));

        // 7. Historique
        mockMvc.perform(get("/comptes/" + sourceId + "/historique")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3)); // 1 depot, 1 retrait, 1 virement
    }

    @Test
    void testOperationsCompte_ErrorCases() throws Exception {
        // Créer le compte
        Map<String, String> payload = Map.of("typeCompte", "courant");
        String responseSource = mockMvc.perform(post("/comptes")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andReturn().getResponse().getContentAsString();
        Integer sourceId = com.jayway.jsonpath.JsonPath.read(responseSource, "$.id");

        // Retrait qui dépasse le solde
        Map<String, Double> retraitPayload = Map.of("montant", 9999.0);
        mockMvc.perform(post("/comptes/" + sourceId + "/retrait")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(retraitPayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Solde insuffisant pour ce retrait."));

        // Compte inexistant (détails)
        mockMvc.perform(get("/comptes/999")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Compte non trouvé avec l'id : 999"));

        // Compte inexistant (historique)
        mockMvc.perform(get("/comptes/999/historique")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isBadRequest());

        // Compte inexistant (dépôt)
        mockMvc.perform(post("/comptes/999/depot")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("montant", 100.0))))
                .andExpect(status().isBadRequest());

        // Virement même compte
        Map<String, Object> virementPayload = Map.of("montant", 10.0, "destinataireId", sourceId);
        mockMvc.perform(post("/comptes/" + sourceId + "/virement")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(virementPayload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAccesRefuse() throws Exception {
        // Créer un utilisateur "Autre" pour générer un compte n'appartenant pas à "testUser"
        Utilisateur autreUser = new Utilisateur(null, "Autre", "User", "autre@test.com", passwordEncoder.encode("pass"), 25, new Date());
        autreUser = utilisateurRepository.sauvegarder(autreUser);
        
        Compte autreCompte = new Compte(null, autreUser.getId(), "courant", 100.0, new Date());
        autreCompte = compteRepository.sauvegarder(autreCompte);
        Integer autreId = autreCompte.getId();

        // 1. Get Details
        mockMvc.perform(get("/comptes/" + autreId)
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Accès refusé."));

        // 2. Get Historique
        mockMvc.perform(get("/comptes/" + autreId + "/historique")
                .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isForbidden());

        // 3. Depot
        mockMvc.perform(post("/comptes/" + autreId + "/depot")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("montant", 10.0))))
                .andExpect(status().isForbidden());

        // 4. Retrait
        mockMvc.perform(post("/comptes/" + autreId + "/retrait")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("montant", 10.0))))
                .andExpect(status().isForbidden());

        // 5. Virement
        mockMvc.perform(post("/comptes/" + autreId + "/virement")
                .header("Authorization", "Bearer " + validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("montant", 10.0, "destinataireId", autreId))))
                .andExpect(status().isForbidden());
    }
}
