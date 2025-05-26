# ADR 004 – Choix du système de messagerie : Apache Kafka

**Statut** : Accepté  
**Date** : 2025-05-26  
**Contexte du projet** : Système de réservation de parking pour une entreprise en environnement hybride.

## Contexte

Une exigence fonctionnelle du projet est la suivante :

> Lorsqu’une réservation est effectuée, un message doit être envoyé dans une file pour qu’un autre service (ex : notification/email) puisse envoyer une confirmation à l’utilisateur.

Cela implique l’introduction d’un **système de messagerie asynchrone** dans l’architecture. Il doit être robuste, compatible avec un environnement conteneurisé, et bien intégré avec l’écosystème Java/Quarkus.

## Décision

Le système de messagerie retenu est **Apache Kafka**, intégré via l’extension `quarkus-smallrye-reactive-messaging-kafka`.

## Alternatives considérées

| Alternative           | Raisons de rejet |
|------------------------|------------------|
| **RabbitMQ**           | Très bon choix pour du message brokering, mais moins adapté aux scénarios d’event streaming ou scalabilité horizontale. |
| **ActiveMQ**           | Plus lourd, moins moderne. Intégration moins fluide avec Quarkus. |
| **Google Pub/Sub / AWS SNS** | Solutions cloud trop spécifiques. Le projet doit rester portable et local. |
| **Utiliser une table en base pour les événements** | Pas scalable, moins réactif, logique asynchrone difficile à maintenir. |

## Raisons du choix

1. **Scalabilité et performance**  
   Kafka est conçu pour le **traitement massif de messages**, très performant même en cas de charge élevée.

2. **Intégration native avec Quarkus**  
   L’extension `quarkus-smallrye-reactive-messaging-kafka` permet de produire et consommer des messages facilement avec un minimum de configuration.

3. **Résilience**  
   Kafka est tolérant aux pannes grâce à la réplication des partitions. Il permet de relire les messages, idéal pour du traitement asynchrone robuste.

4. **Support du modèle événementiel**  
   Kafka est adapté à une architecture orientée événements, qui peut évoluer vers un système plus modulaire (ex : microservices ou services de traitement indépendants).

5. **Déploiement container-friendly**  
   Kafka peut être déployé dans Docker (avec Zookeeper ou KRaft), ce qui permet un setup local de test ou d’intégration facile avec le reste du projet.

6. **Séparation des responsabilités**  
   Kafka permet de découpler la logique de réservation de la logique d’envoi de notification (mail), ce qui suit le principe de responsabilité unique.

## Conséquences

- Un **topic Kafka `reservation-confirmation`** sera créé pour publier les événements de réservation.
- À chaque création de réservation, un message sera produit contenant les données utiles (utilisateur, date, numéro de place, etc.).
- Un service consommateur Kafka pourra lire ces messages pour déclencher l’envoi d’un e-mail (hors du périmètre du projet principal).
- Le projet devra inclure une configuration Kafka dans `application.properties` et un broker Kafka dans le `docker-compose.yml`.
- Des tests d’intégration Kafka seront prévus pour s’assurer du bon fonctionnement de la production de messages.

## Notes supplémentaires

- Le choix de Kafka prépare aussi le projet à une future évolution vers une architecture orientée événements ou microservices.
- Pour le contexte, l’usage de Kafka reste simple (un topic, un producteur), mais réaliste et enrichissant pour apprendre à intégrer de la communication asynchrone.
