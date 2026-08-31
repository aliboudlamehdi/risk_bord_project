# TODO / pistes d'amélioration

Points non traités ou simplifiés faute de temps, et évolutions possibles.

## Fonctionnel

- **Conversion de devises** : `aggregatedExposureBySector` (et la vue agrégée du dashboard) somme les `usedAmount` bruts sans tenir compte de la devise. Le jeu de données fourni mélange des devises au sein d'un même secteur (ex : Banking contient de l'EUR et du CHF) — une vraie agrégation nécessiterait une conversion vers une devise pivot.
- **Concurrence sur approve/reject** : aucune gestion d'optimistic locking. Deux utilisateurs pourraient valider/rejeter la même demande en parallèle sans détection de conflit.
- **Historique des dérogations** : pas d'écran listant les demandes déjà traitées (APPROVED/REJECTED), uniquement les demandes en attente.
- **Confirmation avant action** : les boutons Valider/Rejeter agissent immédiatement, sans modale de confirmation.
- **Pagination serveur** : `GET /api/risk-limits` et `GET /api/derogation-requests` retournent l'intégralité des données ; suffisant à l'échelle du test mais à revoir pour un vrai volume de données (pagination + tri côté serveur).

## Technique

- **Authentification/autorisation** : explicitement hors périmètre selon l'énoncé, mais `requestedBy` reste un champ libre non vérifié — à sécuriser si l'authentification est ajoutée.
- **Persistance** : H2 en mémoire, toutes les données sont perdues au redémarrage. Le profil `docker-compose` pourrait être étendu avec un service PostgreSQL pour une persistance réelle.
- **Documentation API** : pas d'OpenAPI/Swagger exposé.
- **Tests end-to-end** : uniquement des tests unitaires (backend JUnit, frontend Vitest). Pas de tests e2e (Cypress/Playwright) qui auraient permis de valider les parcours utilisateur dans un vrai navigateur.
- **Validation upload CSV côté frontend** : seule l'extension `.csv` est vérifiée avant envoi ; pas de contrôle de taille de fichier.
- **i18n** : tous les libellés sont en français en dur, pas de structure de traduction.
