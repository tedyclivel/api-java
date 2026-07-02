"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { fetchApi, setToken } from "@/services/api";
import Link from "next/link";

export default function Login() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await fetchApi("/login", {
        method: "POST",
        body: JSON.stringify({ email, motDePasse: password }),
      });

      if (response && response.token) {
        setToken(response.token);
        window.location.href = "/dashboard";
      } else {
        throw new Error("Token manquant dans la réponse");
      }
    } catch (err: any) {
      setError(err.message || "Identifiants incorrects");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="container flex-center" style={{ minHeight: "80vh" }}>
      <div className="glass-panel" style={{ width: "100%", maxWidth: "400px" }}>
        <h2 className="text-gradient">Connexion</h2>
        <p className="text-secondary mb-4">Accédez à vos comptes NeoBank</p>
        
        {error && <div className="alert alert-error">{error}</div>}
        
        <form onSubmit={handleLogin}>
          <div className="input-group">
            <label htmlFor="email">Adresse Email</label>
            <input
              id="email"
              type="email"
              className="input-field"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="input-group">
            <label htmlFor="password">Mot de passe</label>
            <input
              id="password"
              type="password"
              className="input-field"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          
          <button type="submit" className="btn btn-primary" style={{ width: "100%" }} disabled={loading}>
            {loading ? "Connexion..." : "Se connecter"}
          </button>
        </form>
        
        <p className="mt-4 text-center text-secondary" style={{ fontSize: "0.9rem" }}>
          Nouveau sur NeoBank ? <Link href="/register" style={{ color: "var(--accent-primary)" }}>Créer un compte</Link>
        </p>
      </div>
    </main>
  );
}
