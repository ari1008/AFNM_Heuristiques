# ADR 001 – Choix du framework backend : Quarkus (Java)

**Statut** : Accepté  
**Date** : 2025-05-26  
**Contexte du projet** : Système de réservation de parking pour une entreprise en environnement hybride.

## Contexte

Le système de réservation de parking à mettre en place est une application web qui devra :

- Gérer des utilisateurs avec des rôles et droits distincts (employés, secrétaires, managers)
- Permettre des réservations de créneaux, des vérifications de présence, la consultation de statistiques, et la gestion par QR Code
- Être scalable, performant, testable et déployable dans des conteneurs
- Produire des événements asynchrones (ex : message en file pour confirmation email)
- S’intégrer dans un contexte DevOps avec containers, CI/CD, et tests automatisés

## Décision

Le framework **Quarkus** avec le langage **Java** a été choisi comme base pour le développement de l’application backend.

## Alternatives considérées

| Alternative         | Raisons de rejet |
|---------------------|------------------|
| **Spring Boot**     | Plus lourd au démarrage, consommation mémoire plus élevée. Moins optimisé pour le cloud natif et les containers. |
| **Node.js/Express** | Peu adapté à notre équipe qui a plus d’expérience avec Java. Moins robuste pour les règles complexes métier. |
| **Micronaut**       | Moins de maturité de l’écosystème. Quarkus est plus activement supporté et documenté actuellement. |

## Raisons du choix

1. **Performance à froid et à chaud**  
   Quarkus est optimisé pour les environnements cloud, avec des temps de démarrage très rapides et une faible empreinte mémoire. Cela le rend idéal pour les containers et les architectures serverless ou CI/CD rapides.

2. **Écosystème Java**  
   L’équipe maîtrise Java. Quarkus permet d’utiliser les standards (JPA/Hibernate, RESTEasy, Panache, etc.) tout en gardant une approche moderne.

3. **Productivité**  
   Quarkus offre un mode développement live très pratique pour itérer rapidement avec `quarkus:dev`, ainsi qu’un générateur de code (dev UI, CLI, extensions) accélérant le prototypage.

4. **Intégration native avec les containers**  
   L’image Docker de l’application sera légère, compatible avec GraalVM pour un binaire natif si besoin.

5. **Réactivité et architecture modulaire**  
   Compatible avec une architecture réactive (Mutiny, Vert.x) pour les traitements asynchrones comme l’envoi de messages en file.

6. **Support Cloud et Kafka**  
   Intégration facile avec Kafka pour les messages de confirmation (exigence du PO), gestion simple de la config avec MicroProfile Config.

7. **Testabilité**  
   Tests unitaires (JUnit), tests d’intégration avec RestAssured, mock facile des dépendances.

## Conséquences

- Le projet utilisera **Maven** comme gestionnaire de dépendances.
- Il sera packagé en **JAR exécutable** ou en **binaire natif** (si performances critiques).
- Le développement suivra une approche **RESTful API**, avec séparation claire frontend/backend.
- Il faudra maîtriser les outils et concepts propres à Quarkus (Panache, injection CDI, configuration MicroProfile, etc.).
- On privilégiera les extensions natives (quarkus-hibernate-orm, quarkus-security, quarkus-smallrye-reactive-messaging pour Kafka…).

## Notes supplémentaires

- Ce choix est aligné avec les objectifs pédagogiques du projet : application modulaire, testable, conteneurisée, et moderne.
- Ce framework est aussi un bon compromis entre performance et maintenabilité pour une application d’entreprise de taille moyenne.
