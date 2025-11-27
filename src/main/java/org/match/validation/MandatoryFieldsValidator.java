package org.match.validation;

import org.match.models.EntrySpectateurDto;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class MandatoryFieldsValidator implements SpectateurValidator {
    @Override
    public void validate(EntrySpectateurDto dto) throws ValidationException {
        if (!StringUtils.hasText(dto.getSpectatorId())) {
            throw new ValidationException("ID Spectateur manquant");
        }
        if (!StringUtils.hasText(dto.getMatchId())) {
            throw new ValidationException("ID Match manquant");
        }
        // Vérification de la structure imbriquée
        if (dto.getSeatLocation() == null) {
            throw new ValidationException("Localisation du siège manquante");
        }
    }
}