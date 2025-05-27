#!/bin/bash

set -e

echo "🚀 Démarrage de la base de données avec Docker Compose..."
docker-compose up -d

echo "📦 Compilation du backend..."
cd backend
./mvnw clean package -DskipTests -Dquarkus.package.type=uber-jar

echo "🏃 Lancement de l'application backend Quarkus..."
java -jar target/*-runner.jar &
BACKEND_PID=$!
cd ..

echo "🌐 Démarrage du frontend Angular..."
cd frontend
npx ng serve

kill $BACKEND_PID
