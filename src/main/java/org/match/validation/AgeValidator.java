package org.match.validation;

import org.match.models.EntrySpectateurDto;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class AgeValidator implements SpectateurValidator {
    @Override
    public void validate(EntrySpectateurDto dto) throws ValidationException {
        if (dto.getAge() == null || dto.getAge() < 0) {
            throw new ValidationException("Age invalide pour le spectateur : " + dto.getSpectatorId());
        }
        if (dto.getAge() < 5) {
            throw new ValidationException("Spectateur trop jeune : " + dto.getAge());
        }
    }
}