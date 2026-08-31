# RiskBoard

Application de suivi des limites de risque et de gestion des demandes de dérogation pour les équipes Sales d'une banque.

- **Backend** : Java 21, Spring Boot 4, Spring Data JPA, H2 (base en mémoire)
- **Frontend** : Angular 22, formulaires réactifs, HttpClient
- **Build** : Maven (backend), npm (frontend)

## Prérequis

- Java 21+
- Maven 3.9+
- Node.js ≥ 22.22.3 ou ≥ 24.15.0 (requis par Angular CLI 22), npm 10+

## Backend

```bash
# Build + tests
mvn clean install

# Lancer l'application (port 8080)
mvn spring-boot:run

# Tests unitaires uniquement
mvn test
```

Au démarrage :
- API : http://localhost:8080/api
- Console H2 : http://localhost:8080/h2-console (JDBC URL : `jdbc:h2:mem:riskboard`, user `sa`, mot de passe vide)
- La base est réinitialisée à chaque redémarrage (H2 en mémoire, aucune donnée persistée).

### Principaux endpoints

| Méthode | URL | Description |
|---|---|---|
| POST | `/api/risk-limits/import` | Import CSV (multipart, champ `file`) |
| GET | `/api/risk-limits` | Liste détaillée des limites (avec usageRate/alertLevel calculés) |
| GET | `/api/risk-limits/sector-exposure?limitType=` | Exposition agrégée par secteur pour un type de limite |
| GET | `/api/counterparties` | Liste des contreparties |
| POST | `/api/derogation-requests` | Création d'une demande de dérogation |
| GET | `/api/derogation-requests?status=` | Liste des demandes (filtrable par statut) |
| GET | `/api/derogation-requests/limit-check` | Vérification existence de limite / seuil 150% (validator async frontend) |
| PATCH | `/api/derogation-requests/{id}/approve` | Valider une demande |
| PATCH | `/api/derogation-requests/{id}/reject` | Rejeter une demande |

## Frontend

```bash
cd frontend
npm install

# Lancer le serveur de dev (port 4200, proxy /api vers localhost:8080)
npm start
# équivalent : ng serve

# Build de production
npm run build

# Tests unitaires (Vitest, exécutés en environnement jsdom, pas de navigateur requis)
npm test
```

Le frontend attend le backend démarré sur `http://localhost:8080` (proxy configuré dans `frontend/proxy.conf.json`).

## Tester l'application de bout en bout

1. Démarrer le backend (`mvn spring-boot:run`) puis le frontend (`npm start` dans `frontend/`).
2. Ouvrir http://localhost:4200.
3. Onglet **Import CSV** : importer `sample-data/risk-limits-sample.csv` (jeu de données fourni dans l'énoncé, couvrant les 3 niveaux d'alerte GREEN/ORANGE/RED).
4. Onglet **Dashboard** : consulter le tableau détaillé, trier les colonnes, filtrer par nom, basculer vers la vue agrégée par secteur via le sélecteur de type de limite.
5. Onglet **Nouvelle dérogation** : soumettre une demande (le formulaire vérifie en temps réel l'existence d'une limite et le seuil de 150%).
6. Onglet **Demandes en attente** : valider ou rejeter la demande créée.

## Docker

```bash
docker compose up --build
```

- Backend exposé sur http://localhost:8080
- Frontend (servi par nginx, proxy `/api` vers le backend) sur http://localhost:4200

## CI

Le pipeline `.gitlab-ci.yml` build et teste indépendamment le backend (Maven) et le frontend (npm), en deux étapes (`build` puis `test`), avec mise en cache des dépendances (`.m2/repository`, `frontend/.npm`).

## Documentation complémentaire

- [TODO.md](./TODO.md) : pistes d'amélioration et points non traités faute de temps.