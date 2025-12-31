package org.match.validation;


import org.match.models.EntrySpectateurDto;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompositeSpectateurValidator implements SpectateurValidator {

    private final List<SpectateurValidator> validators;

    public CompositeSpectateurValidator(List<SpectateurValidator> validators) {
        this.validators = validators.stream()
                .filter(v -> !(v instanceof CompositeSpectateurValidator))
                .toList();
    }

    @Override
    public void validate(EntrySpectateurDto dto) throws ValidationException {
        for (SpectateurValidator validator : validators) {
            validator.validate(dto);
        }
    }
}

/**
 * 1. Le Problème (SANS Composite)
 * Imaginez que vous avez créé 3 règles (Strategies) : AgeValidator, EmailValidator, TicketValidator.
 *
 * Sans le Composite, votre SpectateurProcessor serait obligé de connaître et d'appeler chaque règle individuellement.
 *
 * Code "Sale" (Sans Composite) :
 *
 * public class SpectateurProcessor implements ItemProcessor<...> {
 *
 *     // Le Processor dépend de TOUTES les règles
 *     private AgeValidator ageValidator;
 *     private EmailValidator emailValidator;
 *     private TicketValidator ticketValidator;
 *     // Et si vous en ajoutez 10 autres ?
 *     .....
 *     Le problème ici : Le Processor (le client) sait "trop de choses". Il sait combien il y a de règles et lesquelles.
 *     Si vous changez les règles, vous devez toucher au code du Processor.(OPEN/CLOSED PRINCIPLLE)
 *
 * 2. La Solution (AVEC Composite)
 * Le Pattern Composite permet de traiter un groupe d'objets (plusieurs validateurs) de la même manière qu'un seul objet.
 *
 * Le CompositeValidator est une boîte qui contient toutes les règles.
 *
 * Il implémente la même interface (SpectateurValidator).
 *
 * Pour le Processor, le Composite ressemble à n'importe quel autre validateur.
 *
 * Code "Propre" (Avec Composite) :
 *
 * Java
 *
 * public class SpectateurProcessor implements ItemProcessor<...> {
 *
 *     // Le Processor ne connaît qu'UN SEUL interlocuteur
 *     private SpectateurValidator validator;
 *
 *     public SpectateurDto process(SpectateurDto item) {
 *         // "Valide-moi cet item". Je ne veux pas savoir comment tu fais, ni combien de règles il y a.
 *         validator.validate(item);
 *
 *         return item;
 *     }
 * }
 * Ce qui se passe dans le Composite (La magie) :
 *
 * Java
 *
 * public class CompositeSpectateurValidator implements SpectateurValidator {
 *     private List<SpectateurValidator> rules; // Contient Age, Email, Ticket...
 *
 *     public void validate(SpectateurDto item) {
 *         // C'est lui qui fait la boucle, pas le Processor !
 *         for (SpectateurValidator rule : rules) {
 *             rule.validate(item);
 *         }
 *     }
 * }
 *
 *
 */