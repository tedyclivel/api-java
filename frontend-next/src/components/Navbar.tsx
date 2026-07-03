"use client";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { getToken, removeToken } from "@/services/api";

export default function Navbar() {
  const router = useRouter();
  const pathname = usePathname();
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  useEffect(() => {
    setIsAuthenticated(!!getToken());
  }, [pathname]);

  const handleLogout = () => {
    removeToken();
    setIsAuthenticated(false);
    router.push("/login");
  };

  return (
    <nav className="navbar container">
      <Link href="/" className="nav-brand">
        <span className="text-gradient">Neo</span>Bank
      </Link>
      
      <div className="nav-links">
        {isAuthenticated ? (
          <>
            <Link href="/dashboard" className="nav-link">Tableau de bord</Link>
            <button onClick={handleLogout} className="btn btn-secondary">Déconnexion</button>
          </>
        ) : (
          <>
            <Link href="/login" className="nav-link">Connexion</Link>
            <Link href="/register" className="btn btn-primary">Créer un compte</Link>
          </>
        )}
      </div>
    </nav>
  );
}
