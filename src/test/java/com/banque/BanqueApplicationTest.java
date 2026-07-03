package com.banque;

import org.junit.jupiter.api.Test;

class BanqueApplicationTest {

    @Test
    void main() {
        // On passe les paramètres en arguments pour ne pas polluer les autres tests
        // On active le profil "test" (pour H2) et on désactive le serveur web
        BanqueApplication.main(new String[] {
            "--spring.profiles.active=test", 
            "--spring.main.web-application-type=none"
        });
    }
}
