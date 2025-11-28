package org.match.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "stat_type", nullable = false)
    private StatisticType statisticType;

    @Column(name = "stat_key")
    private String key;

    @Column(name = "stat_value")
    private Double value;

    private String unit;

    private String description;

    private String matchId;

    private LocalDateTime calculatedAt;
}