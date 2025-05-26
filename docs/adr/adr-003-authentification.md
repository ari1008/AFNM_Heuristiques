# ADR 003 – Choix de la stratégie d’authentification : JWT avec gestion des rôles via Quarkus Security

**Statut** : Accepté  
**Date** : 2025-05-26  
**Contexte du projet** : Système de réservation de parking pour une entreprise en environnement hybride.

## Contexte

L’application devra permettre à différents types d’utilisateurs de se connecter et accéder à des vues et actions différentes selon leur rôle :

- **Employés** : faire des réservations, consulter l’historique, scanner des QR codes pour check-in
- **Secrétaires** : gérer les réservations, voir tout le planning, créer/modifier des utilisateurs
- **Managers** : consulter des statistiques globales, réserver sur un mois complet

Les utilisateurs devront s’authentifier pour accéder à l’application et recevoir une réponse adaptée à leur profil. L’application doit également rester stateless (sans session serveur), et être facilement testable et déployable dans des containers.

## Décision

L’authentification se fera via **JWT (JSON Web Token)** émis à la connexion, avec **gestion des rôles via Quarkus Security**.  

Les rôles (employé, secrétaire, manager) seront intégrés dans le token JWT.

## Alternatives considérées

| Alternative                        | Raisons de rejet |
|------------------------------------|------------------|
| **Sessions HTTP avec cookies**     | Nécessite du state côté serveur, moins adapté au déploiement en containers et à la scalabilité. |
| **OAuth 2 / OpenID Connect via Keycloak** | Trop complexe pour le périmètre de ce projet. Configuration plus lourde. |
| **Basic Auth**                     | Peu sécurisé, non recommandé en production. Ne gère pas les rôles de manière fine. |

## Raisons du choix

1. **Stateless & scalable**  
   JWT permet de garder l’application sans état. Le serveur n’a pas besoin de stocker les sessions. Cela s’intègre bien avec une architecture conteneurisée et distribuée.

2. **Intégration native avec Quarkus**  
   Quarkus Security fournit un support direct pour JWT via l’extension `quarkus-smallrye-jwt`. Cela permet de sécuriser les endpoints REST avec des annotations (`@RolesAllowed`, `@Authenticated`...).

3. **Simplicité d’implémentation**  
   Génération simple de tokens JWT à la connexion. Le token contient les rôles, l’expiration, et les métadonnées utilisateur utiles (nom, email...).

4. **Contrôle précis des accès**  
   Les rôles intégrés dans le token permettent de sécuriser facilement les endpoints et d’afficher des vues différentes selon le profil.

5. **Interopérabilité**  
   JWT est un standard largement supporté, compatible avec les outils de frontend, API Gateway ou autres services futurs.

6. **Expérience utilisateur fluide**  
   Une fois connecté, l’utilisateur n’a pas besoin de se reconnecter tant que son token est valide.

## Conséquences

- L’authentification sera mise en place via un endpoint `/auth/login` qui génère un token JWT après vérification des identifiants.
- Les utilisateurs seront stockés dans la base PostgreSQL avec hachage sécurisé des mots de passe (ex : Bcrypt).
- Les rôles seront attribués et inclus dans le token lors de la connexion.
- Les routes seront sécurisées par rôle avec des annotations sur les endpoints REST.
- Le frontend devra stocker et envoyer le JWT dans l’en-tête `Authorization: Bearer <token>` à chaque requête.

## Notes supplémentaires

- Un système de **renouvellement de token (refresh)** pourra être ajouté si nécessaire.
- Pour une production réelle, une solution de délégation d’authentification comme Keycloak pourrait être envisagée, mais est jugée surdimensionnée ici.
