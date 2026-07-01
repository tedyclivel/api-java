package com.banque.application.port.out;

import com.banque.domain.model.Historique;
import java.util.List;

public interface HistoriqueRepository {
    Historique sauvegarder(Historique historique);
    List<Historique> trouverParCompteId(Integer compteId);
}
