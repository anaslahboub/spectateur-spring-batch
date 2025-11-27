package org.match.validation;

import org.match.models.EntrySpectateurDto;
import org.springframework.batch.infrastructure.item.validator.ValidationException;

@FunctionalInterface
public interface SpectateurValidator {
    /**
     * Valide un spectateur.
     * @param dto L'objet à valider
     * @throws ValidationException si une règle n'est pas respectée
     */
    void validate(EntrySpectateurDto dto) throws ValidationException;
}