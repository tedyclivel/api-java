package com.banque.domain.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "historique")
public class Historique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "compte_id", nullable = false)
    private Integer compteId;

    private String type; // 'DEPOT', 'RETRAIT', 'VIREMENT_ENVOYE', 'VIREMENT_RECU'
    private double montant;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "autre_partie_id")
    private Integer autrePartieId;

    public Historique() {}

    public Historique(Integer id, Integer compteId, String type, double montant, Date date, Integer autrePartieId) {
        this.id = id;
        this.compteId = compteId;
        this.type = type;
        this.montant = montant;
        this.date = date;
        this.autrePartieId = autrePartieId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getCompteId() { return compteId; }
    public void setCompteId(Integer compteId) { this.compteId = compteId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Integer getAutrePartieId() { return autrePartieId; }
    public void setAutrePartieId(Integer autrePartieId) { this.autrePartieId = autrePartieId; }
}
