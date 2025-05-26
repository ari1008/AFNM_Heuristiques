# ADR 002 – Choix de la base de données : PostgreSQL

**Statut** : Accepté  
**Date** : 2025-05-26  
**Contexte du projet** : Système de réservation de parking pour une entreprise en environnement hybride.

## Contexte

L’application de réservation de parking devra :

- Gérer des entités métier structurées (utilisateurs, rôles, places de parking, réservations, historiques)
- Appliquer des règles métiers complexes (créneaux limités, check-in, gestion des places électriques)
- Assurer l’historisation des données (audits, statistiques)
- Proposer des jointures efficaces pour les tableaux de bord (taux d’occupation, types de véhicules, etc.)

Une base de données **relationnelle** est donc la solution la plus adaptée à ce modèle de données structuré et aux besoins analytiques.

## Décision

Le SGBD choisi est **PostgreSQL**.

## Alternatives considérées

| Alternative     | Raisons de rejet |
|----------------|------------------|
| **MySQL/MariaDB** | Moins riche fonctionnellement (types JSONB, index GIN). Moins de fonctionnalités avancées sur la gestion des types et des contraintes. |
| **MongoDB**        | Non relationnel. Moins adapté aux transactions, aux relations complexes et à l’historisation rigoureuse. |
| **H2** (in-memory) | Convient uniquement aux tests ou au prototypage. Pas adapté à la production. |

## Raisons du choix

1. **Modèle relationnel solide**  
   PostgreSQL est un SGBD relationnel éprouvé, parfaitement adapté aux données fortement structurées et interconnectées comme les réservations de places de parking.

2. **Fiabilité et conformité ACID**  
   PostgreSQL garantit la fiabilité des transactions (ACID), essentielle pour les opérations critiques comme la réservation ou l’annulation de créneaux.

3. **Support des types avancés**  
   Prise en charge de types modernes comme `JSONB`, `ARRAY`, et `UUID`, utiles pour certains champs dynamiques ou identifiants uniques.

4. **Richesse fonctionnelle**  
   Prise en charge des contraintes d’intégrité, des fonctions de fenêtrage (ex : pour les dashboards) et des index performants.

5. **Compatibilité avec Quarkus et Panache ORM**  
   PostgreSQL est bien supporté par **Hibernate ORM** et **Panache** via Quarkus. Il est facile à intégrer avec les extensions `quarkus-hibernate-orm-panache` et `quarkus-jdbc-postgresql`.

6. **Déploiement conteneurisé facile**  
   Disponible sous forme d’image officielle Docker. Facilement intégrable dans un `docker-compose.yml` ou un environnement Kubernetes.

## Conséquences

- Le schéma de la base de données sera défini dès le départ et versionné via des migrations (ex : Flyway ou Liquibase).
- PostgreSQL sera lancé dans un conteneur Docker pour les environnements de développement et de test.
- L’identifiant unique des entités sera un `UUID` généré côté application pour éviter les conflits.
- Les relations (OneToMany, ManyToOne, etc.) seront gérées via JPA/Hibernate.
- Des index seront créés sur les colonnes critiques pour les performances (date de réservation, numéro de place, utilisateur, etc.).

## Notes supplémentaires

- Le choix de PostgreSQL est aligné avec les exigences du projet (règles métier fortes, audit, tableaux de bord analytiques).
- Ce SGBD open source est largement utilisé en entreprise, ce qui garantit la portabilité et la maintenabilité du projet dans le futur.
