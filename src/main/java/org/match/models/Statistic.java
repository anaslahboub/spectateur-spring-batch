package org.match.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stat_key", nullable = false)
    private String key; // Ex: "TOTAL_SPECTATEURS", "TOP_NATIONALITE_MAROC"

    @Column(name = "stat_value")
    private String value; // Ex: "15000", "45%"

    private LocalDateTime calculatedAt;
}