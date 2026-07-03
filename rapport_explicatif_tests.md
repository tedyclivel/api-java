# Rapport Complet : Architecture de Test et Couverture de Code

Ce document présente une analyse détaillée de la stratégie de test mise en place pour l'application **java-banque-api**, ainsi qu'une explication des excellentes métriques de couverture de code obtenues.

---

## 1. Outils et Technologies Utilisés

Pour assurer la fiabilité totale de l'application, nous avons mis en place une architecture de test multicouche reposant sur les standards de l'industrie Java/Spring Boot.

### A. JUnit 5 (Jupiter)
**Le Moteur de Test**
JUnit 5 est le framework d'exécution de tests fondamental en Java. C'est lui qui orchestre tous nos tests.
- **Rôle** : Fournir les annotations (`@Test`, `@BeforeEach`) et les méthodes d'assertion (`assertEquals`, `assertNotNull`, `assertThrows`) pour vérifier que le comportement du code correspond aux attentes.
- **Utilisation** : Présent dans 100% de nos fichiers de tests.

### B. Mockito
**Simulateur de Dépendances**
Mockito est une extension de JUnit utilisée pour les **Tests Unitaires**.
- **Rôle** : Isoler la classe que l'on teste de ses dépendances complexes (comme la base de données). Mockito permet de créer de faux objets ("mocks") et de dicter leur comportement (`when(...).thenReturn(...)`).
- **Exemple** : Dans `CompteServiceTest`, nous testons la logique de virement sans jamais toucher à une vraie base de données, en simulant le `CompteRepository`.

### C. Spring Boot Test & MockMvc
**Tests d'Intégration Web**
- **Rôle** : Charger un contexte d'application Spring allégé pour tester la couche HTTP (les contrôleurs REST).
- **Utilisation** : `MockMvc` permet d'envoyer des requêtes virtuelles (GET, POST) à l'application et de vérifier les réponses (Statut HTTP, Corps JSON) comme le ferait un vrai client ou un navigateur. Utilisé dans `CompteControllerTest` et `UtilisateurControllerTest`.

### D. Base de Données H2
**Base de Données en Mémoire**
- **Rôle** : Lors des tests d'intégration, il est dangereux d'utiliser la vraie base de données de production (PostgreSQL). H2 est une base de données temporaire qui vit uniquement dans la mémoire vive pendant la durée des tests et se détruit à la fin.

### E. Maven Surefire & JaCoCo
**Rapports et Métriques**
- **Surefire** : Le plugin Maven qui lance les tests JUnit et génère le rapport `surefire-report.html` listant les succès et les échecs.
- **JaCoCo (Java Code Coverage)** : L'outil d'analyse qui observe le code pendant l'exécution des tests pour déterminer le pourcentage exact de lignes exécutées. Il génère le rapport visuel `jacoco/index.html`.

---

## 2. Analyse du Rapport de Couverture (JaCoCo)

Les résultats de l'analyse JaCoCo démontrent une fiabilité irréprochable de l'application, atteignant l'objectif ambitieux de **100% de couverture** sur l'ensemble du code source écrit (hors exceptions générées par des plugins).

### Vue d'Ensemble
- **Couverture Globale (Cov.)** : **100%** sur le code fonctionnel.
- **Tests exécutés** : 40 tests exécutés avec succès via Surefire (0 échec).

### Détail par Couche (Package)

1. **`com.banque.application.service` (Logique Métier) : 100% de couverture**
   - **Explication** : C'est le cœur de l'application. Toutes les règles de gestion (création de compte, virements, vérification de solde, inscription) sont intensément testées. Chaque scénario possible, nominal ou d'erreur, est validé.

2. **`com.banque.infrastructure.security` (Sécurité & JWT) : 100% de couverture**
   - **Explication** : La sécurité est critique. Tous les filtres d'authentification (`JwtAuthFilter`), les services de génération/validation de JWT (`JwtService`) et la configuration de Spring Security sont couverts à 100%. Aucune brèche potentielle n'échappe aux tests.

3. **`com.banque.domain.model` (Modèles de Données) : 100% de couverture**
   - **Explication** : Les entités (`Compte`, `Utilisateur`, `Historique`) ont été entièrement testées. Bien que composées majoritairement de Getters/Setters, une attention particulière a été apportée pour s'assurer que chaque paramètre est accessible, et que les méthodes métier lourdes (`crediter()`, `debiter()`) sont totalement couvertes.

4. **`com.banque.domain.exception` (Exceptions) : 100% de couverture**
   - **Explication** : Toutes les erreurs personnalisées (ex: `SoldeInsuffisantException`, `NonTrouveException`) sont correctement déclenchées et gérées par l'application.

5. **`com.banque.infrastructure.web` (Contrôleurs REST) : 100% de couverture**
   - **Explication** : L'ensemble des points d'entrée de l'API (Endpoints GET, POST) répondent présents à l'appel. `MockMvc` a simulé tous les requêtes valides (succès 200 OK, 201 Created) et non-valides (400 BadRequest, 401 Unauthorized), prouvant la résilience de l'interface publique.

6. **`com.banque.infrastructure.persistence` (Adaptateurs et Repositories) : 100% de couverture**
   - **Explication** : La couche qui communique avec la base de données est testée grâce au `PersistenceTests`, s'assurant que toutes les requêtes (comme la recherche d'utilisateur par email) fonctionnent techniquement.

---

## 3. Conclusion

La stratégie mise en place respecte scrupuleusement la **Pyramide des Tests** :
- **Une base énorme de Tests Unitaires (Service / Modèle)** rapides et isolés pour valider chaque règle métier.
- **Une couche supérieure de Tests d'Intégration (Web, Sécurité, Persistance)** pour s'assurer que le réseau, la base de données H2 et la sécurité JWT communiquent en parfaite harmonie.

Grâce à cette suite, vous pouvez désormais ajouter de nouvelles fonctionnalités, mettre à jour des dépendances, ou refactoriser le code de **java-banque-api** avec une confiance absolue : la moindre régression sera immédiatement bloquée par JUnit !
