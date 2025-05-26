# ADR 005 – Choix du framework frontend : Angular avec TypeScript

**Statut** : Accepté  
**Date** : 2025-05-26  
**Contexte du projet** : Application web de réservation de parking, destinée à des utilisateurs non techniques, avec plusieurs profils et interfaces spécifiques (employé, secrétaire, manager).

## Contexte

L’application est une **web app** qui doit permettre aux utilisateurs :

- De consulter les places disponibles et faire des réservations
- De scanner un QR code et confirmer un check-in
- De consulter leur historique de réservations
- D’accéder à des interfaces différentes selon leur profil (vue admin, statistiques, gestion utilisateurs)
- De bénéficier d’une expérience fluide, réactive, claire et ergonomique

Le frontend doit être développé en complément d’une API REST, dans une architecture bien séparée backend / frontend. Il doit être structuré, testable, maintenable et conteneurisable.

## Décision

Le framework choisi pour le frontend est **Angular** avec le langage **TypeScript**.

## Alternatives considérées

| Alternative           | Raisons de rejet |
|------------------------|------------------|
| **React (JS/TS)**      | Très souple mais moins structurant pour un projet en équipe débutante. Nécessite plus de choix et de discipline architecturale. |
| **Vue.js**             | Très bon framework mais moins utilisé dans les contextes d'entreprise ou d'applications complexes. |
| **Svelte**             | Léger et moderne, mais encore jeune, moins d’outillage, documentation moins mature. |
| **Vanilla JS / jQuery** | Obsolète pour une application moderne, complexe à maintenir à moyen terme. |

## Raisons du choix

1. **Structure forte et conventions claires**  
   Angular impose une architecture claire (modules, composants, services), ce qui est bénéfique pour le travail en équipe et la lisibilité du code.

2. **Support TypeScript natif**  
   Angular est conçu en TypeScript, ce qui apporte une sécurité typée, une autocomplétion riche et une meilleure maintenabilité.

3. **Écosystème complet out of the box**  
   Angular intègre directement un routeur, un système de formulaires, de gestion des états, d’injection de dépendance et d’outils de tests unitaires/E2E.

4. **Séparation claire des responsabilités**  
   Grâce aux services, directives et composants, Angular permet une bonne modularisation du code, avec des responsabilités bien séparées.

5. **Intégration facile avec l’API REST backend**  
   Angular dispose d’un module HTTP client robuste, idéal pour interagir avec les endpoints REST exposés par Quarkus.

6. **Outils de build et de développement intégrés**  
   Angular CLI facilite la génération de composants, le démarrage du projet, la gestion des environnements, le linting, le testing, etc.

7. **Bon support des profils utilisateur**  
   Le système de routage permet de gérer facilement les vues selon les rôles avec des guards (`CanActivate`) et services d’authentification.

8. **Communauté et documentation**  
   Angular est très bien documenté, stable, largement utilisé dans les entreprises et bien soutenu par Google.

## Conséquences

- Le frontend sera développé en Angular (v16+), via Angular CLI.
- Le code sera écrit en TypeScript avec une structure modulaire (features/modules/services).
- Le frontend sera conteneurisé (Docker) et déployable de manière autonome ou derrière un reverse proxy.
- La communication avec le backend se fera via appels HTTP (REST) sécurisés avec JWT.
- Des guards seront mis en place pour restreindre l’accès aux vues selon les rôles utilisateur.

## Notes supplémentaires

- Le choix d’Angular est particulièrement pertinent ici pour encadrer le développement dans un contexte pédagogique et collaboratif, avec une architecture claire.
- TypeScript est un excellent tremplin pour monter en compétences sur des langages fortement typés comme Java.
