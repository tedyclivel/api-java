package com.banque.domain.model;

import jakarta.persistence.*;
import java.util.Date;
import com.banque.domain.exception.SoldeInsuffisantException;

@Entity
@Table(name = "compte")
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "utilisateur_id", nullable = false)
    private Integer utilisateurId;

    @Column(name = "type_compte")
    private String typeCompte;

    private double solde;

    @Column(name = "date_creation")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    public Compte() {}

    public Compte(Integer id, Integer utilisateurId, String typeCompte, double solde, Date dateCreation) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.typeCompte = typeCompte;
        this.solde = solde;
        this.dateCreation = dateCreation;
    }

    public void crediter(double montant) {
        if (montant > 0) {
            this.solde += montant;
        }
    }

    public void debiter(double montant) throws SoldeInsuffisantException {
        if (montant > 0) {
            if (this.solde >= montant) {
                this.solde -= montant;
            } else {
                throw new SoldeInsuffisantException("Solde insuffisant pour ce retrait.");
            }
        }
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUtilisateurId() { return utilisateurId; }
    public void setUtilisateurId(Integer utilisateurId) { this.utilisateurId = utilisateurId; }
    public String getTypeCompte() { return typeCompte; }
    public void setTypeCompte(String typeCompte) { this.typeCompte = typeCompte; }
    public double getSolde() { return solde; }
    public void setSolde(double solde) { this.solde = solde; }
    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }
}
