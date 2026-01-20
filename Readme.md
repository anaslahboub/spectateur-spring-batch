# 🎟️ Spectateur Spring Batch - Système de Gestion des Entrées de Spectateurs

## 📋 Description du Projet

**Spectateur Spring Batch** est une application Spring Boot qui implémente un système de traitement par lots (batch) pour gérer les entrées des spectateurs dans un stade. L'application lit des données depuis des fichiers XML et JSON, les valide, les transforme, les persiste dans une base de données, puis calcule des statistiques détaillées sur la fréquentation.

Ce projet démontre l'utilisation de **Spring Batch** pour traiter des volumes de données de manière robuste, avec gestion des erreurs, validation métier, et génération de statistiques analytiques.

---

## 🏗️ Architecture du Projet

### **Structure Générale**

Le projet suit une architecture en couches classique : 

```
src/main/java/org/match/
├── batch/              # Configuration Spring Batch (Jobs, Steps, Readers, Writers, Processors)
├── factory/            # Factory pour la création d'objets métier
├── models/             # Entités JPA et DTOs
├── repository/         # Repositories Spring Data JPA
├── service/            # Services métier (Statistiques)
├── validation/         # Validateurs métier avec Strategy et Composite patterns
└── ProjectSpringbatchApplication.java
```

---

## ⚙️ Fonctionnalités Principales

### 1. **Traitement Batch Multi-Sources**
- **Lecture XML** : Extraction des données depuis `spectateurs.xml` avec JAXB
- **Lecture JSON** : Extraction des données depuis `spectateurs.json` avec Jackson
- **Validation** : Application de règles métier (âge minimum, champs obligatoires, etc.)
- **Transformation** : Conversion DTO → Entité JPA avec enrichissement (catégorie spectateur)
- **Persistence** :  Sauvegarde en base de données avec JPA

### 2. **Calcul Automatique de Statistiques**
L'application génère automatiquement des statistiques riches :
- Distribution par nationalité
- Répartition par type de ticket (VIP, Standard, Premium, etc.)
- Occupation par porte d'entrée
- Affluence par tribune et par bloc
- Top 10 des spectateurs les plus actifs
- Plages horaires d'entrée (min/max par match)
- Fréquentation par match

### 3. **Ordonnancement Automatique**
- Exécution planifiée toutes les 10 minutes via `@Scheduled`
- Passage automatique de paramètres (ID unique par exécution)

### 4. **Tolérance aux Pannes**
- Gestion des erreurs avec `faultTolerant()`, `retry()`, `skip()`
- Logs détaillés avec SLF4J

---

## 🎨 Design Patterns Implémentés

### 1. **Strategy Pattern** 🎯

**Localisation** : Package `org.match.validation`

**Objectif** : Encapsuler les règles de validation de manière extensible et interchangeable. 

**Implémentation** : 
- **Interface** : `SpectateurValidator` (contrat de validation)
- **Stratégies concrètes** :
  - `AgeValidator` : Valide l'âge (minimum 5 ans)
  - `MandatoryFieldsValidator` : Vérifie les champs obligatoires
  - Autres validateurs peuvent être ajoutés sans modifier le code existant

**Avantages** :
- Respect du principe **Open/Closed** (ouvert à l'extension, fermé à la modification)
- **Single Responsibility** : Chaque validateur a une seule responsabilité
- Facilite les tests unitaires isolés

```java
@FunctionalInterface
public interface SpectateurValidator {
    void validate(EntrySpectateurDto dto) throws ValidationException;
}

@Component
public class AgeValidator implements SpectateurValidator {
    @Override
    public void validate(EntrySpectateurDto dto) throws ValidationException {
        if (dto.getAge() < 5) {
            throw new ValidationException("Spectateur trop jeune");
        }
    }
}
```

---

### 2. **Composite Pattern** 🌳

**Localisation** :  `CompositeSpectateurValidator`

**Objectif** : Traiter un groupe de validateurs comme un seul objet, pour simplifier le client (Processor).

**Implémentation** :
- `CompositeSpectateurValidator` implémente `SpectateurValidator`
- Contient une liste de `SpectateurValidator`
- Délègue l'appel à tous les validateurs enfants

**Avantages** :
- Le `SpectateurProcessor` ne connaît qu'**un seul validateur** (le composite)
- Ajout/suppression de règles sans modifier le Processor
- Évite la pollution du code avec des dizaines de `if/else`

```java
@Component
public class CompositeSpectateurValidator implements SpectateurValidator {
    private final List<SpectateurValidator> validators;

    @Override
    public void validate(EntrySpectateurDto dto) throws ValidationException {
        for (SpectateurValidator validator :  validators) {
            validator.validate(dto);
        }
    }
}
```

---

### 3. **Factory Pattern** 🏭

**Localisation** : Package `org.match.factory`

**Objectif** : Centraliser et encapsuler la logique de création d'objets complexes.

**Implémentation** :
- `EntrySpectateurFactory` : Crée des objets `EntrySpectateur` à partir de DTOs
- `SpectateurFactory` : Crée des objets `Spectateur`

**Avantages** : 
- Séparation de la logique de création et de la logique métier
- Facilite la maintenance et les évolutions
- Respect du principe **Single Responsibility**

```java
public class EntrySpectateurFactory {
    public static EntrySpectateur createEntrySpectateur(EntrySpectateurDto item) {
        Spectateur spectateur = SpectateurFactory.createSpectateur(
            item.getSpectatorId(), 
            item.getAge(), 
            item.getNationality()
        );
        // ...  construction de l'objet complet
        return entrySpectateur;
    }
}
```

---

### 4. **Repository Pattern** 📦

**Localisation** : Package `org.match.repository`

**Objectif** : Abstraire l'accès aux données et découpler la logique métier de la couche de persistance.

**Implémentation** :
- `SpectateurRepository` : Accès aux spectateurs
- `EntrySpectateurRepository` : Accès aux entrées
- `StatisticsRepository` : Accès aux statistiques

**Avantages** :
- Abstraction de la base de données
- Facilite les tests (mocking)
- Centralise les requêtes

```java
@Repository
public interface SpectateurRepository extends JpaRepository<Spectateur, String> {
}
```

---

### 5. **Builder Pattern** 🏗️

**Localisation** :  Entités et DTOs (avec Lombok `@Builder`)

**Objectif** : Faciliter la construction d'objets complexes avec de nombreux attributs.

**Implémentation** :
- Utilisation de Lombok `@Builder` sur les entités : 
  - `Spectateur`
  - `EntrySpectateur`
  - `Statistics`
  - `SeatLocation`

**Avantages** : 
- Code fluide et lisible
- Évite les constructeurs avec trop de paramètres
- Permet de construire des objets partiels

```java
EntrySpectateur entry = EntrySpectateur.builder()
    .matchId("M123")
    .entryTime(LocalDateTime.now())
    .gate("Gate A")
    .spectateur(spectateur)
    .build();
```

---

### 6. **Template Method Pattern** 📝

**Localisation** : Spring Batch (`ItemReader`, `ItemProcessor`, `ItemWriter`)

**Objectif** : Définir le squelette d'un algorithme dans une classe de base, en déléguant certaines étapes aux sous-classes.

**Implémentation** :
- Spring Batch fournit le template (Read → Process → Write)
- Nous implémentons : 
  - `SpectateurJsonReader` (ItemReader)
  - `SpectateurXmlReader` (StaxEventItemReader)
  - `SpectateurProcessor` (ItemProcessor)
  - `SpectateurWritter` (JpaItemWriter)

**Avantages** :
- Framework éprouvé pour le traitement batch
- Gestion automatique des transactions et des erreurs

---

### 7. **Singleton Pattern** ☝️

**Localisation** :  Beans Spring (`@Component`, `@Service`, `@Repository`, `@Configuration`)

**Objectif** : Assurer qu'une classe n'a qu'une seule instance dans le contexte Spring.

**Implémentation** :
- Tous les beans Spring sont des singletons par défaut
- Exemples : `StatisticsService`, `CompositeSpectateurValidator`, `BatchConfig`

**Avantages** :
- Partage d'état (injection de dépendances)
- Performances (une seule instance)

---

### 8. **Observer Pattern (Listener)** 👂

**Localisation** : `SpectateurSkipListener`

**Objectif** :  Réagir aux événements du batch (erreurs, skip, etc.).

**Implémentation** :
- `SpectateurSkipListener` implémente `SkipListener<Object, Object>`
- Enregistre les erreurs lors de la lecture, du traitement, ou de l'écriture

**Avantages** :
- Monitoring et debugging facilités
- Logs structurés des erreurs

```java
@Component
public class SpectateurSkipListener implements SkipListener<Object, Object> {
    @Override
    public void onSkipInRead(Throwable t) {
        log.error("Erreur lors de la lecture", t);
    }
}
```

---

### 9. **Dependency Injection Pattern** 💉

**Localisation** :  Partout (Spring Framework)

**Objectif** : Découpler les dépendances et favoriser la testabilité.

**Implémentation** :
- Injection par constructeur (recommandée)
- Exemples : 
  - `SpectateurProcessor` reçoit `SpectateurValidator` et `EntrySpectateurRepository`
  - `StatisticsService` reçoit les 3 repositories

**Avantages** : 
- Testabilité (mocking facile)
- Flexibilité (changement d'implémentation)
- Couplage faible

---

## 📊 Modèle de Données

### **Entités Principales**

#### **Spectateur**
```java
@Entity
public class Spectateur {
    @Id
    private String spectatorId;
    private Integer age;
    private String nationality;
    @Enumerated(EnumType.STRING)
    private SpectatorCategory spectatorCategory; // PREMIERE_VISITE, OCCASIONNEL, REGULIER, SUPER_FAN
}
```

#### **EntrySpectateur**
```java
@Entity
public class EntrySpectateur {
    @Id
    @GeneratedValue
    private Long id;
    private String matchId;
    private LocalDateTime entryTime;
    private String gate;
    private String ticketNumber;
    @Enumerated(EnumType. STRING)
    private TicketType ticketType; // STANDARD, VIP, PREMIUM, INVITATION, LOGE, MEDIA
    @Embedded
    private SeatLocation seatLocation; // tribune, bloc, rang, siège
    @ManyToOne
    private Spectateur spectateur;
}
```

#### **Statistics**
```java
@Entity
public class Statistics {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType. STRING)
    private StatisticType statisticType;
    private String key;
    private Double value;
    private String unit;
    private String description;
    private String matchId;
    private LocalDateTime calculatedAt;
}
```

---

## 🔄 Flux d'Exécution du Batch

```
1. Déclenchement (Scheduler toutes les 10 min)
         ↓
2. Job "spectateurJob"
         ↓
    ┌─────────────────┐
    │  Step 1: XML    │ → Lecture XML → Validation → Transformation → Écriture DB (chunk:  4)
    └─────────────────┘
         ↓
    ┌─────────────────┐
    │  Step 2: JSON   │ → Lecture JSON → Validation → Transformation → Écriture DB (chunk: 10)
    └─────────────────┘
         ↓
    ┌─────────────────────────┐
    │  Step 3: Statistiques   │ → Calcul de toutes les stats → Sauvegarde DB (Tasklet)
    └─────────────────────────┘
         ↓
3.  Fin de l'exécution (logs de succès/erreur)
```

---

## 🚀 Configuration et Démarrage

### **Prérequis**
- Java 17+
- Maven 3.8+
- Base de données (H2 en mémoire ou PostgreSQL/MySQL)

### **Lancement avec Docker Compose**

Le projet inclut un fichier `docker-compose.yml` pour la base de données : 

```bash
# Démarrer la base de données
docker-compose up -d

# Lancer l'application
./mvnw spring-boot:run
```

### **Configuration**

Les fichiers de configuration se trouvent dans `src/main/resources` : 
- `application.properties` ou `application.yml` : Configuration Spring Boot
- `input/spectateurs.xml` : Données XML à traiter
- `input/spectateurs.json` : Données JSON à traiter

---

## 📈 Statistiques Générées

| Type de Statistique | Description |
|---------------------|-------------|
| **NATIONALITY_DISTRIBUTION** | Nombre de spectateurs par nationalité |
| **NATIONALITY_PERCENTAGE** | Pourcentage par nationalité |
| **TICKET_TYPE_DISTRIBUTION** | Répartition des types de tickets |
| **TICKET_TYPE_PERCENTAGE** | Pourcentage par type de ticket |
| **GATE_OCCUPANCY** | Occupation par porte d'entrée |
| **TRIBUNE_ATTENDANCE** | Affluence par tribune |
| **BLOC_ATTENDANCE** | Affluence par bloc |
| **MATCH_ATTENDANCE** | Fréquentation totale par match |
| **TOP_ACTIVE_SPECTATORS** | Top 10 des spectateurs les plus actifs |
| **ENTRY_TIME_MIN/MAX** | Plages horaires d'entrée par match |

---

## 🛠️ Technologies Utilisées

| Technologie | Version | Usage |
|-------------|---------|-------|
| **Spring Boot** | 3.x | Framework principal |
| **Spring Batch** | 5.x | Traitement par lots |
| **Spring Data JPA** | 3.x | Persistance |
| **Hibernate** | 6.x | ORM |
| **H2 / PostgreSQL** | - | Base de données |
| **Jackson** | 2.x | Parsing JSON |
| **JAXB** | 4.x | Parsing XML |
| **Lombok** | 1.18+ | Réduction boilerplate |
| **SLF4J / Logback** | 2.x | Logging |
| **Maven** | 3.8+ | Build |
| **Docker** | - | Conteneurisation |

---

## 🧪 Tests

Le projet inclut des tests unitaires et d'intégration :

```bash
# Exécuter tous les tests
./mvnw test

# Avec couverture de code
./mvnw verify
```

---

## 📝 Logs et Monitoring

L'application génère des logs détaillés dans le répertoire `logs/` :

```
📖 Chargement du fichier JSON:  input/spectateurs.json
✅ 50 spectateurs chargés depuis le JSON
🔄 Traitement du spectateur ID: SP001
✅ Spectateur ID: SP001 - Historique:  3, Nouvelle catégorie: SPECTATEUR_REGULIER
📊 Début du calcul des statistiques dérivées... 
✅ 125 statistiques calculées et enregistrées avec succès en 234 ms
✅ Batch terminé avec succès
```

---

## 📚 Principes SOLID Appliqués

| Principe | Application dans le Projet |
|----------|----------------------------|
| **S** - Single Responsibility | Chaque validateur, service et repository a une seule responsabilité |
| **O** - Open/Closed | Ajout de validateurs sans modifier le code existant (Strategy) |
| **L** - Liskov Substitution | Tous les validateurs sont interchangeables via l'interface `SpectateurValidator` |
| **I** - Interface Segregation | Interfaces petites et ciblées (`SpectateurValidator`, `ItemProcessor`) |
| **D** - Dependency Inversion | Dépendance sur des abstractions (interfaces) plutôt que des implémentations concrètes |

---

## 🎯 Points Clés du Projet

### **Extensibilité**
- Ajout de nouvelles sources de données (CSV, API, etc.) :  créer un nouveau `ItemReader`
- Ajout de règles de validation : créer une nouvelle classe implémentant `SpectateurValidator`
- Ajout de statistiques : ajouter une méthode dans `StatisticsService`

### **Robustesse**
- Gestion des erreurs avec `faultTolerant()`, `retry()`, `skip()`
- Validation métier avant persistence
- Transactions gérées par Spring Batch

### **Performance**
- Traitement par chunks (4 pour XML, 10 pour JSON)
- Utilisation de `@Transactional` pour optimiser les accès DB
- Lazy loading avec JPA

### **Maintenabilité**
- Code découplé grâce aux design patterns
- Logs structurés pour le debugging
- Configuration externalisée

---

## 👥 Auteur

**Anas Lahboub**
- Repository:  [anaslahboub/spectateur-spring-batch](https://github.com/anaslahboub/spectateur-spring-batch)

---

## 📄 Licence

Ce projet est un exemple pédagogique démontrant l'utilisation de Spring Batch et des design patterns. 

---

## 🔗 Ressources Complémentaires

- [Documentation Spring Batch](https://docs.spring.io/spring-batch/docs/current/reference/html/)
- [Design Patterns (Gang of Four)](https://en.wikipedia.org/wiki/Design_Patterns)
- [Principes SOLID](https://en.wikipedia.org/wiki/SOLID)
