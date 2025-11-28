# spectateur-spring-batch

Résumé
-----
Ce dépôt contient le mini-projet "Spectateur - Spring Batch". Le PDF `miniProjet.pdf` inclus dans le dépôt est le rapport complet du projet (description des objectifs, architecture, conception du batch et résultat attendu). Ce README propose les instructions essentielles pour comprendre, construire et exécuter l'application Spring Batch fournie.

Objectif du projet
------------------
- Implémenter un traitement batch (Spring Batch) pour lire, traiter et persister des données liées aux "spectateurs" (voir le rapport pour le contexte métier).
- Fournir une architecture claire pour l'exécution de jobs, la configuration des sources (fichiers, base de données) et le suivi d'exécution.

Contenu du dépôt
----------------
- `src/` : code source Java (Spring Boot + Spring Batch).
- `pom.xml` (ou `build.gradle`) : configuration de build.
- `application.yml` / `application.properties` : configuration (datasource, batch, chemins de fichiers).
- `miniProjet.pdf` : rapport détaillé du mini-projet (analyse, diagrammes, cas de tests).
- `README.md` : ce fichier.

Prérequis
---------
- Java 11+ (ou version précise mentionnée dans le POM)
- Maven (ou Gradle selon le projet)
- Base de données (H2 embarquée par défaut pour tests, ou Postgres/MySQL en production)
- (Facultatif) Docker si vous préférez conteneuriser/exécuter la base

Installation & compilation
--------------------------
1. Cloner le dépôt :
   ```bash
   git clone https://github.com/anaslahboub/spectateur-spring-batch.git
   cd spectateur-spring-batch
   ```
2. Construire avec Maven :
   ```bash
   mvn clean package
   ```
   ou pour lancer directement avec Spring Boot :
   ```bash
   mvn spring-boot:run
   ```

Configuration
-------------
- Par défaut, le projet peut être configuré pour utiliser une BD H2 en mémoire. Vérifiez `src/main/resources/application.yml` (ou `application.properties`).
- Paramètres usuels à vérifier / définir :
    - `spring.datasource.*` : url, username, password (pour Postgres/MySQL)
    - `spring.batch.job.enabled` : true/false
    - chemins d'entrée (input file), format (CSV/JSON) et séparateurs
- Si vous utilisez une base externe (Postgres/MySQL), créez la base et modifiez `application.yml` en conséquence.

Exécution d'un job Spring Batch
-------------------------------
- Après compilation, pour exécuter le job via l'exécutable jar :
  ```bash
  java -jar target/spectateur-spring-batch-<version>.jar
  ```
- Ou pour passer des paramètres au job :
  ```bash
  java -jar target/spectateur-spring-batch-<version>.jar --spring.batch.job.names=nomDuJob input.file=/chemin/vers/fichier.csv
  ```
- En développement vous pouvez aussi lancer depuis l'IDE (classe annotée `@SpringBootApplication`).

Structure typique du batch
--------------------------
- Job -> Step(s)
- Reader (ex : FlatFileItemReader pour CSV)
- Processor (logique métier, validations, transformations)
- Writer (ex : JdbcBatchItemWriter pour persister en BD)
- Gestion des erreurs (skip, retry) et listeners pour traçabilité
  Consultez le rapport `miniProjet.pdf` pour les diagrammes et explications détaillées.

Base de données & schema
------------------------
- Le projet peut créer automatiquement les tables Spring Batch si `spring.batch.schema` est configuré.
- Exemple pour Postgres :
  ```yaml
  spring:
    datasource:
      url: jdbc:postgresql://localhost:5432/spectateur
      username: user
      password: pass
    jpa:
      hibernate:
        ddl-auto: update
  ```
- Pour tests rapides, H2 in-memory est conseillé.

Tests
-----
- Lancer les tests unitaires / d'intégration :
  ```bash
  mvn test
  ```

Docker (optionnel)
------------------
- Vous pouvez dockeriser l'application en ajoutant un `Dockerfile` :
    - Build l'artefact via Maven
    - Copier le jar et exécuter `java -jar`
- Fournir aussi un `docker-compose.yml` pour orchestrer la BD + l'application si besoin.

Bonnes pratiques & conseils
---------------------------
- Séparer les configurations par profile (`application-dev.yml`, `application-prod.yml`).
- Externaliser les paramètres (URL de la BD, chemins d'input) en variables d'environnement pour CI/CD.
- Ajouter des métriques / logs structurés pour surveiller les jobs (via Actuator, micrometer).

Contribution
------------
- Fork -> Feature branch -> PR vers `main`
- Ecrire des tests pour toute nouvelle fonctionnalité
- Documenter les changements dans le `miniProjet.pdf` si la portée du rapport évolue

Licence
-------
- Aucune licence définie dans le dépôt. Ajouter un fichier `LICENSE` si vous souhaitez en appliquer une (MIT, Apache 2.0, ...).

Aide & contact
--------------
- Auteur / propriétaire du dépôt : anaslahboub
- Pour mise à jour du README ou ajout d'une PR automatique, dites-moi si vous voulez que je crée la PR et je peux préparer les fichiers.

Notes finales
--------------
Ce README est une proposition générée à partir du rapport `miniProjet.pdf` présent dans le dépôt. Pour enrichir le README avec des exemples concrets (échantillon de fichier d'entrée, commandes exactes utilisées dans le rapport, schémas précis), je peux récupérer ou extraire des extraits précis du PDF et intégrer ces éléments dans le README sur demande.