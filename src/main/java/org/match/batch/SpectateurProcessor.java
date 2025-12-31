package org.match.batch;

import lombok.extern.slf4j.Slf4j;
import org.match.factory.EntrySpectateurFactory;
import org.match.models.*;
import org.match.repository.EntrySpectateurRepository;
import org.match.repository.SpectateurRepository;
import org.match.validation.SpectateurValidator;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SpectateurProcessor implements ItemProcessor<EntrySpectateurDto, EntrySpectateur> {

    private final SpectateurValidator validator;
    private final EntrySpectateurRepository  entrySpectateurRepository;

    public SpectateurProcessor(SpectateurValidator compositeSpectateurValidator, EntrySpectateurRepository entrySpectateurRepository) {
        this.validator = compositeSpectateurValidator;
        this.entrySpectateurRepository = entrySpectateurRepository;
    }

    @Override
    public EntrySpectateur process(EntrySpectateurDto item) throws Exception {
        log.debug("🔄 Traitement du spectateur ID: {}", item.getSpectatorId());

        validator.validate(item);

        EntrySpectateur entrySpectateur = EntrySpectateurFactory.createEntrySpectateur(item);

        long historique = entrySpectateurRepository.countEntriesBySpectatorId(item.getSpectatorId());
        long totalMatchs = historique + 1;

        SpectatorCategory category = SpectatorCategory.fromMatchCount(totalMatchs);
        entrySpectateur.getSpectateur().setSpectatorCategory(category);

        log.debug("✅ Spectateur ID: {} - Historique: {}, Nouvelle catégorie: {}",
                item.getSpectatorId(), historique, category);

        return entrySpectateur;
    }
}

/**
 * j'ai choisi de combiner le pattern Strategy et le pattern Composite pour éviter de polluer le ItemProcessor avec des dizaines de if/else.
 * Mon objectif était de rendre le code extensible et facile à tester."
 *
 * Voici les détails pour chaque pattern :
 *
 * 1. Pourquoi le Pattern Strategy ? (L'Interface SpectateurValidator)
 * Argument clé : Le Principe Open/Closed (OCP).
 *
 * "J'ai utilisé le pattern Strategy pour respecter le principe Open/Closed. Si demain nous devons ajouter une nouvelle règle (ex: vérifier la nationalité),
 * je peux créer une nouvelle classe NationalityValidator sans modifier le code existant du Processor ni des autres validateurs."
 *
 * Argument secondaire : Single Responsibility (SRP).
 *
 * "Chaque classe de validation ne fait qu'une seule chose (ex: AgeValidator ne vérifie que l'âge). Cela rend le code plus clair et permet de faire des tests unitaires très précis sur chaque règle."
 */