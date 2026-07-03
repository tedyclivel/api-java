"use client";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { fetchApi } from "@/services/api";

export default function Dashboard() {
  const router = useRouter();
  
  const [comptes, setComptes] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  
  // Compte sélectionné
  const [selectedCompteId, setSelectedCompteId] = useState<number | null>(null);
  const [historique, setHistorique] = useState<any[]>([]);
  
  // UI states
  const [activeTab, setActiveTab] = useState("historique"); // historique | depot | retrait | virement
  const [actionLoading, setActionLoading] = useState(false);
  const [actionMsg, setActionMsg] = useState({ text: "", type: "" });
  
  // Forms states
  const [montant, setMontant] = useState("");
  const [compteDestId, setCompteDestId] = useState("");
  const [nouveauTypeCompte, setNouveauTypeCompte] = useState("epargne");

  useEffect(() => {
    loadComptes();
  }, []);

  const loadComptes = async (preserveSelection = false) => {
    try {
      const data = await fetchApi("/mes-comptes");
      setComptes(data);
      if (data.length > 0 && !preserveSelection && !selectedCompteId) {
        setSelectedCompteId(data[0].id);
        loadHistorique(data[0].id);
      }
    } catch (err: any) {
      setError("Impossible de charger vos comptes");
    } finally {
      setLoading(false);
    }
  };

  const loadHistorique = async (compteId: number) => {
    try {
      const data = await fetchApi(`/comptes/${compteId}/historique`);
      setHistorique(data);
    } catch (err: any) {
      console.error("Erreur historique", err);
    }
  };

  const handleSelectCompte = (id: number) => {
    setSelectedCompteId(id);
    setActionMsg({ text: "", type: "" });
    loadHistorique(id);
  };

  const handleCreerCompte = async () => {
    setActionLoading(true);
    try {
      await fetchApi("/comptes", {
        method: "POST",
        body: JSON.stringify({ typeCompte: nouveauTypeCompte })
      });
      await loadComptes(true);
      setActionMsg({ text: "Nouveau compte créé avec succès !", type: "success" });
    } catch (err: any) {
      setActionMsg({ text: err.message || "Erreur création", type: "error" });
    } finally {
      setActionLoading(false);
    }
  };

  const handleAction = async (endpoint: string, extraBody = {}) => {
    if (!montant || parseFloat(montant) <= 0) {
      setActionMsg({ text: "Veuillez entrer un montant valide", type: "error" });
      return;
    }
    
    setActionLoading(true);
    setActionMsg({ text: "", type: "" });

    try {
      await fetchApi(endpoint, {
        method: "POST",
        body: JSON.stringify({ montant: parseFloat(montant), ...extraBody })
      });
      setActionMsg({ text: "Opération réussie !", type: "success" });
      setMontant("");
      setCompteDestId("");
      await loadComptes(true);
      if (selectedCompteId) loadHistorique(selectedCompteId);
    } catch (err: any) {
      setActionMsg({ text: err.message || "Erreur lors de l'opération", type: "error" });
    } finally {
      setActionLoading(false);
    }
  };

  const formatDate = (dateStr: string) => {
    if (!dateStr) return "N/A";
    return new Date(dateStr).toLocaleString('fr-FR');
  };

  if (loading) {
    return (
      <main className="container page-transition" style={{ paddingBottom: "4rem" }}>
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "2rem" }}>
          <div>
            <div className="skeleton skeleton-text" style={{ width: "200px", height: "2.5rem" }}></div>
            <div className="skeleton skeleton-text" style={{ width: "300px" }}></div>
          </div>
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr", gap: "2rem" }}>
          <div>
            <div className="glass-panel" style={{ height: "400px" }}>
              <div className="skeleton skeleton-row"></div>
              <div className="skeleton skeleton-row"></div>
              <div className="skeleton skeleton-row"></div>
            </div>
          </div>
          <div>
            <div className="glass-panel" style={{ height: "400px" }}>
              <div className="skeleton skeleton-row"></div>
              <div className="skeleton skeleton-row" style={{ height: "200px" }}></div>
            </div>
          </div>
        </div>
      </main>
    );
  }

  const selectedCompte = comptes.find(c => c.id === selectedCompteId);

  return (
    <main className="container page-transition" style={{ paddingBottom: "4rem" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "2rem" }}>
        <div>
          <h1 className="text-gradient" style={{ marginBottom: "0.5rem" }}>Tableau de bord</h1>
          <p className="text-secondary">Gérez vos comptes et effectuez des opérations.</p>
        </div>
        <div style={{ display: "flex", gap: "1rem", alignItems: "center" }}>
          <select 
            className="input-field" 
            style={{ width: "auto" }}
            value={nouveauTypeCompte}
            onChange={(e) => setNouveauTypeCompte(e.target.value)}
          >
            <option value="epargne">Épargne</option>
            <option value="courant">Courant</option>
          </select>
          <button className="btn btn-secondary" onClick={handleCreerCompte} disabled={actionLoading}>
            + Nouveau Compte
          </button>
        </div>
      </div>
      
      {error && <div className="alert alert-error">{error}</div>}

      <div style={{ display: "grid", gridTemplateColumns: "1fr 2fr", gap: "2rem" }}>
        
        {/* Colonne de gauche : Liste des comptes */}
        <div>
          <h2 className="mb-2">Vos comptes</h2>
          {comptes.length === 0 ? (
            <div className="glass-panel text-secondary">
              Vous n'avez pas encore de compte.
            </div>
          ) : (
            <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
              {comptes.map((c) => (
                <div 
                  key={c.id} 
                  className="glass-panel" 
                  style={{ 
                    cursor: "pointer",
                    borderLeft: selectedCompteId === c.id ? "4px solid var(--accent-primary)" : "1px solid var(--border-color)",
                    backgroundColor: selectedCompteId === c.id ? "rgba(99, 102, 241, 0.1)" : "var(--bg-card)",
                    transition: "all 0.2s"
                  }}
                  onClick={() => handleSelectCompte(c.id)}
                >
                  <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <div>
                      <h3 style={{ margin: 0, fontSize: "1.2rem", textTransform: "capitalize" }}>{c.typeCompte}</h3>
                      <p className="text-secondary" style={{ fontSize: "0.9rem", margin: 0 }}>ID Compte : {c.id}</p>
                    </div>
                    <div style={{ fontSize: "1.3rem", fontWeight: "bold" }}>
                      {c.solde.toFixed(2)} €
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Colonne de droite : Détails & Actions */}
        {selectedCompte && (
          <div className="glass-panel" style={{ display: "flex", flexDirection: "column" }}>
            
            {/* Header du compte */}
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem", paddingBottom: "1.5rem", borderBottom: "1px solid var(--border-color)" }}>
              <div>
                <h2 style={{ margin: 0, textTransform: "capitalize" }}>Compte {selectedCompte.typeCompte} (N° {selectedCompte.id})</h2>
                <p className="text-secondary">Solde disponible</p>
              </div>
              <h1 className="text-gradient" style={{ margin: 0 }}>{selectedCompte.solde.toFixed(2)} €</h1>
            </div>

            {/* Onglets d'action */}
            <div style={{ display: "flex", gap: "1rem", marginBottom: "2rem" }}>
              <button className={`btn ${activeTab === 'historique' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setActiveTab('historique')}>Historique</button>
              <button className={`btn ${activeTab === 'depot' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setActiveTab('depot')}>Dépôt</button>
              <button className={`btn ${activeTab === 'retrait' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setActiveTab('retrait')}>Retrait</button>
              <button className={`btn ${activeTab === 'virement' ? 'btn-primary' : 'btn-secondary'}`} onClick={() => setActiveTab('virement')}>Virement</button>
            </div>

            {/* Messages */}
            {actionMsg.text && (
              <div className={`alert alert-${actionMsg.type} mb-4`}>
                {actionMsg.text}
              </div>
            )}

            {/* Contenu Historique */}
            {activeTab === 'historique' && (
              <div style={{ overflowX: "auto" }}>
                {historique.length === 0 ? (
                  <p className="text-secondary text-center py-4">Aucune transaction trouvée.</p>
                ) : (
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Date</th>
                        <th>Type</th>
                        <th>Montant</th>
                      </tr>
                    </thead>
                    <tbody>
                      {historique.map((h, i) => (
                        <tr key={i}>
                          <td>{formatDate(h.date)}</td>
                          <td>
                            <span style={{ 
                              padding: "4px 8px", 
                              borderRadius: "4px", 
                              fontSize: "0.85rem",
                              backgroundColor: h.type === 'DEPOT' || h.type === 'VIREMENT_RECU' ? 'rgba(16, 185, 129, 0.2)' : 'rgba(239, 68, 68, 0.2)',
                              color: h.type === 'DEPOT' || h.type === 'VIREMENT_RECU' ? '#6ee7b7' : '#fca5a5'
                            }}>
                              {h.type}
                            </span>
                          </td>
                          <td style={{ fontWeight: "bold", color: h.type === 'DEPOT' || h.type === 'VIREMENT_RECU' ? '#6ee7b7' : '#fca5a5' }}>
                            {h.type === 'DEPOT' || h.type === 'VIREMENT_RECU' ? '+' : '-'}{h.montant.toFixed(2)} €
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </div>
            )}

            {/* Contenu Dépôt */}
            {activeTab === 'depot' && (
              <div style={{ maxWidth: "400px" }}>
                <div className="input-group">
                  <label>Montant à déposer (€)</label>
                  <input type="number" className="input-field" value={montant} onChange={(e) => setMontant(e.target.value)} placeholder="Ex: 100.00" step="0.01" />
                </div>
                <button className="btn btn-primary" onClick={() => handleAction(`/comptes/${selectedCompte.id}/depot`)} disabled={actionLoading}>
                  Valider le dépôt
                </button>
              </div>
            )}

            {/* Contenu Retrait */}
            {activeTab === 'retrait' && (
              <div style={{ maxWidth: "400px" }}>
                <div className="input-group">
                  <label>Montant à retirer (€)</label>
                  <input type="number" className="input-field" value={montant} onChange={(e) => setMontant(e.target.value)} placeholder="Ex: 50.00" step="0.01" />
                </div>
                <button className="btn btn-primary" onClick={() => handleAction(`/comptes/${selectedCompte.id}/retrait`)} disabled={actionLoading}>
                  Valider le retrait
                </button>
              </div>
            )}

            {/* Contenu Virement */}
            {activeTab === 'virement' && (
              <div style={{ maxWidth: "400px" }}>
                <div className="input-group">
                  <label>N° de compte destinataire (ID)</label>
                  <input type="number" className="input-field" value={compteDestId} onChange={(e) => setCompteDestId(e.target.value)} placeholder="Ex: 2" />
                </div>
                <div className="input-group">
                  <label>Montant à virer (€)</label>
                  <input type="number" className="input-field" value={montant} onChange={(e) => setMontant(e.target.value)} placeholder="Ex: 25.00" step="0.01" />
                </div>
                <button className="btn btn-primary" onClick={() => handleAction(`/comptes/${selectedCompte.id}/virement`, { destinataireId: parseInt(compteDestId) })} disabled={actionLoading}>
                  Envoyer le virement
                </button>
              </div>
            )}

          </div>
        )}
      </div>
    </main>
  );
}
