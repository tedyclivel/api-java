"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { fetchApi } from "@/services/api";
import Link from "next/link";

export default function Register() {
  const router = useRouter();
  const [formData, setFormData] = useState({
    nom: "",
    prenom: "",
    email: "",
    age: "",
    motDePasse: ""
  });
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.id]: e.target.value });
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      await fetchApi("/utilisateurs/register", {
        method: "POST",
        body: JSON.stringify({
          ...formData,
          age: parseInt(formData.age)
        }),
      });

      setSuccess(true);
      setTimeout(() => {
        router.push("/login");
      }, 2000);
    } catch (err: any) {
      setError(err.message || "Erreur lors de l'inscription");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="container flex-center" style={{ minHeight: "80vh" }}>
      <div className="glass-panel" style={{ width: "100%", maxWidth: "450px", marginTop: "2rem", marginBottom: "2rem" }}>
        <h2 className="text-gradient">Inscription</h2>
        <p className="text-secondary mb-4">Rejoignez la banque de demain</p>
        
        {error && <div className="alert alert-error">{error}</div>}
        {success && <div className="alert alert-success">Inscription réussie ! Redirection...</div>}
        
        {!success && (
          <form onSubmit={handleRegister}>
            <div style={{ display: "flex", gap: "1rem" }}>
              <div className="input-group" style={{ flex: 1 }}>
                <label htmlFor="prenom">Prénom</label>
                <input id="prenom" type="text" className="input-field" value={formData.prenom} onChange={handleChange} required />
              </div>
              <div className="input-group" style={{ flex: 1 }}>
                <label htmlFor="nom">Nom</label>
                <input id="nom" type="text" className="input-field" value={formData.nom} onChange={handleChange} required />
              </div>
            </div>
            
            <div className="input-group">
              <label htmlFor="age">Âge</label>
              <input id="age" type="number" className="input-field" value={formData.age} onChange={handleChange} required min="18" />
            </div>

            <div className="input-group">
              <label htmlFor="email">Adresse Email</label>
              <input id="email" type="email" className="input-field" value={formData.email} onChange={handleChange} required />
            </div>
            
            <div className="input-group">
              <label htmlFor="motDePasse">Mot de passe</label>
              <input id="motDePasse" type="password" className="input-field" value={formData.motDePasse} onChange={handleChange} required minLength={6} />
            </div>
            
            <button type="submit" className="btn btn-primary mt-2" style={{ width: "100%" }} disabled={loading}>
              {loading ? "Création..." : "Créer mon compte"}
            </button>
          </form>
        )}
        
        <p className="mt-4 text-center text-secondary" style={{ fontSize: "0.9rem" }}>
          Déjà client ? <Link href="/login" style={{ color: "var(--accent-primary)" }}>Connectez-vous</Link>
        </p>
      </div>
    </main>
  );
}
