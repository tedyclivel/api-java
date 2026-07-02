import Link from "next/link";

export default function Home() {
  return (
    <main className="container flex-center" style={{ minHeight: "80vh", flexDirection: "column", textAlign: "center" }}>
      <div style={{ maxWidth: "800px" }}>
        <h1 style={{ fontSize: "4rem", marginBottom: "1.5rem" }}>
          L'avenir de la <span className="text-gradient">Banque</span> est ici.
        </h1>
        <p className="text-secondary mb-4" style={{ fontSize: "1.2rem", lineHeight: "1.6" }}>
          Découvrez NeoBank, l'API bancaire de nouvelle génération. Gérez vos comptes, 
          effectuez des virements instantanés et gardez le contrôle total sur vos finances 
          avec notre plateforme sécurisée.
        </p>
        
        <div style={{ display: "flex", gap: "1rem", justifyContent: "center", marginTop: "2rem" }}>
          <Link href="/register" className="btn btn-primary" style={{ padding: "1rem 2rem", fontSize: "1.1rem" }}>
            Ouvrir un compte
          </Link>
          <Link href="/login" className="btn btn-secondary" style={{ padding: "1rem 2rem", fontSize: "1.1rem" }}>
            Espace Client
          </Link>
        </div>
      </div>
      
      <div style={{ display: "grid", gridTemplateColumns: "repeat(3, 1fr)", gap: "2rem", marginTop: "6rem", textAlign: "left" }}>
        <div className="glass-panel">
          <h3 className="text-gradient mb-2">Sécurité maximale</h3>
          <p className="text-secondary">Vos données sont protégées par un chiffrement de bout en bout et des tokens JWT sécurisés.</p>
        </div>
        <div className="glass-panel">
          <h3 className="text-gradient mb-2">Temps réel</h3>
          <p className="text-secondary">Virements instantanés et historiques mis à jour en temps réel via notre API performante.</p>
        </div>
        <div className="glass-panel">
          <h3 className="text-gradient mb-2">100% Cloud</h3>
          <p className="text-secondary">Infrastructure moderne hébergée sur Render et connectée à Supabase (PostgreSQL).</p>
        </div>
      </div>
    </main>
  );
}
