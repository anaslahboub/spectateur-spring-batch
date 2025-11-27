package org.match.models;

import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
@Builder
@Entity
public class Spectateur {

    @Id
    @Column(name = "spectator_id")
    private String spectatorId;

    private Integer age;

    private String nationality;

    @Enumerated(EnumType.STRING)
    private SpectatorCategory spectatorCategory;
}
