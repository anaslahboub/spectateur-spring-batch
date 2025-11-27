package org.match.factory;

import org.match.models.Spectateur;

public class SpectateurFactory {

    public static Spectateur createSpectateur(String spectateurId, Integer age, String nationality) {

        return Spectateur.builder()
                .spectatorId(spectateurId)
                .age(age)
                .nationality(nationality)
                .build();
    }



}
