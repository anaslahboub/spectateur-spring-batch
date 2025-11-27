package org.match.models;


import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
@Builder
@Embeddable
public class SeatLocation {
    private String tribune;
    private String bloc;
    private Integer rang;
    private Integer siege;
}