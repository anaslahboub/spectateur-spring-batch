package org.match.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.match.models.*;
import org.match.repository.EntrySpectateurRepository;
import org.match.repository.SpectateurRepository;
import org.match.repository.StatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final SpectateurRepository spectateurRepository;
    private final EntrySpectateurRepository entrySpectateurRepository;

    @Transactional
    public void calculateAllStatistics() {
        log.info("📊 Début du calcul des statistiques dérivées...");
        long startTime = System.currentTimeMillis();

        try {
            List<Statistics> allStats = new ArrayList<>();

            // Ajouter les statistiques de chaque catégorie
            log.debug("Calcul des statistiques par nationalité...");
            allStats.addAll(calculateNationalityStatistics());
            log.debug("Calcul des statistiques par type de ticket...");
            allStats.addAll(calculateTicketTypeStatistics());
            log.debug("Calcul des statistiques d'occupation des portes...");
            allStats.addAll(calculateGateOccupancyStatistics());
            log.debug("Calcul des statistiques par tribune...");
            allStats.addAll(calculateTribuneStatistics());
            log.debug("Calcul des statistiques par bloc...");
            allStats.addAll(calculateBlocStatistics());
            log.debug("Calcul des statistiques de fréquentation des matchs...");
            allStats.addAll(calculateMatchAttendanceStatistics());
            log.debug("Calcul du top 10 des spectateurs les plus actifs...");
            allStats.addAll(calculateTop10ActiveSpectators());
            log.debug("Calcul des statistiques par plage horaire d'entrée...");
            allStats.addAll(calculateEntryTimeRangeStatistics());

            log.info("Sauvegarde de {} statistiques dans la base de données...", allStats.size());
            statisticsRepository.saveAll(allStats);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("✅ {} statistiques calculées et enregistrées avec succès en {} ms",
                    allStats.size(), duration);

        } catch (Exception e) {
            log.error("❌ Erreur lors du calcul des statistiques", e);
            throw e;
        }
    }


    private List<Statistics> calculateNationalityStatistics() {
        List<Spectateur> spectateurs = spectateurRepository.findAll();

        if (spectateurs.isEmpty()) {
            return Collections.emptyList();
        }
        int totalSpectateurs = spectateurs.size();
        List<Statistics> allStatsNationality = new ArrayList<>();
        Map<String, Long> countByNationality = spectateurs.stream()
                .collect(Collectors.groupingBy(
                        Spectateur::getNationality,
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByNationality.entrySet()) {
            String nationality = entry.getKey();
            Long count = entry.getValue();
            double percentage = (count * 100.0) / totalSpectateurs;
            Statistics distribution = Statistics.builder()
                    .calculatedAt(LocalDateTime.now())
                    .key(nationality)
                    .statisticType(StatisticType.NATIONALITY_DISTRIBUTION)
                    .description("Nombre de spectateurs de nationalité " + nationality)
                    .unit("count")
                    .value(count.doubleValue())
                    .build();

            Statistics percentageStat = Statistics.builder()
                    .calculatedAt(LocalDateTime.now())
                    .key(nationality)
                    .statisticType(StatisticType.NATIONALITY_PERCENTAGE)
                    .description("Pourcentage de spectateurs de nationalité " + nationality)
                    .unit("%")
                    .value(percentage)
                    .build();

            allStatsNationality.add(distribution);
            allStatsNationality.add(percentageStat);
        }

        return allStatsNationality;
    }

    private List<Statistics> calculateTicketTypeStatistics() {
        List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        int totalEntries = entrySpectateurs.size();
        List<Statistics> allStatsTicketType = new ArrayList<>();

        Map<String, Long> countByTicketType = entrySpectateurs.stream()
                .filter(e -> e.getTicketType() != null) // Filtrer les null
                .collect(Collectors.groupingBy(
                        e -> e.getTicketType().toString(),
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByTicketType.entrySet()) {
            String ticketType = entry.getKey();
            Long count = entry.getValue();
            double percentage = (count * 100.0) / totalEntries;

            Statistics distribution = Statistics.builder()
                    .statisticType(StatisticType.TICKET_TYPE_DISTRIBUTION)
                    .description("Nombre de tickets de type " + ticketType)
                    .unit("count")
                    .value(count.doubleValue())
                    .calculatedAt(LocalDateTime.now())
                    .key(ticketType)
                    .build();

            Statistics percentageStat = Statistics.builder()
                    .key(ticketType)
                    .statisticType(StatisticType.TICKET_TYPE_PERCENTAGE)
                    .description("Pourcentage de tickets de type " + ticketType)
                    .unit("%")
                    .calculatedAt(LocalDateTime.now())
                    .value(percentage)
                    .build();

            allStatsTicketType.add(distribution);
            allStatsTicketType.add(percentageStat);
        }

        return allStatsTicketType;
    }

    private List<Statistics> calculateGateOccupancyStatistics() {
        List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        int totalEntries = entrySpectateurs.size();
        List<Statistics> allStatsGate = new ArrayList<>();

        Map<String, Long> countByGate = entrySpectateurs.stream()
                .filter(e -> e.getGate() != null)
                .collect(Collectors.groupingBy(
                        EntrySpectateur::getGate,
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByGate.entrySet()) {
            String gate = entry.getKey();
            Long count = entry.getValue();
            double percentage = (count * 100.0) / totalEntries;

            Statistics distribution = Statistics.builder()
                    .key(gate)
                    .calculatedAt(LocalDateTime.now())
                    .statisticType(StatisticType.GATE_OCCUPANCY)
                    .description("Nombre d'entrées par " + gate)
                    .unit("count")
                    .value(count.doubleValue())
                    .build();

            Statistics percentageStat = Statistics.builder()
                    .key(gate)
                    .statisticType(StatisticType.GATE_OCCUPANCY_PERCENTAGE)
                    .description("Pourcentage d'utilisation de " + gate)
                    .unit("%")
                    .calculatedAt(LocalDateTime.now())
                    .value(percentage)
                    .build();

            allStatsGate.add(distribution);
            allStatsGate.add(percentageStat);
        }

        return allStatsGate;
    }


    private List<Statistics> calculateTribuneStatistics() {
        List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsTribune = new ArrayList<>();

        Map<String, Long> countByTribune = entrySpectateurs.stream()
                .filter(e -> e.getSeatLocation() != null)
                .filter(e -> e.getSeatLocation().getTribune() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getSeatLocation().getTribune(),
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByTribune.entrySet()) {
            String tribune = entry.getKey();
            Long count = entry.getValue();

            Statistics distribution = Statistics.builder()
                    .key(tribune)
                    .calculatedAt(LocalDateTime.now())
                    .statisticType(StatisticType.TRIBUNE_DISTRIBUTION)
                    .description("Affluence tribune " + tribune)
                    .unit("count")
                    .value(count.doubleValue())
                    .build();
            allStatsTribune.add(distribution);
        }

        String tribuneMostPopular = countByTribune.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucune tribune");

        Statistics mostPopular = Statistics.builder()
                .key(tribuneMostPopular)
                .calculatedAt(LocalDateTime.now())
                .statisticType(StatisticType.TRIBUNE_ATTENDANCE)
                .description("Tribune la plus populaire : " + tribuneMostPopular)
                .unit("name")
                .value(0.0)
                .build();

        allStatsTribune.add(mostPopular);

        return allStatsTribune;
    }

    private List<Statistics> calculateBlocStatistics() {
        List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsBloc = new ArrayList<>();

        Map<String, Long> countByBloc = entrySpectateurs.stream()
                .filter(e -> e.getSeatLocation() != null)
                .filter(e -> e.getSeatLocation().getTribune() != null)
                .filter(e -> e.getSeatLocation().getBloc() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getSeatLocation().getTribune() + "-" + e.getSeatLocation().getBloc(),
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByBloc.entrySet()) {
            String blocKey = entry.getKey();
            Long count = entry.getValue();

            Statistics blocStat = Statistics.builder()
                    .key(blocKey)
                    .calculatedAt(LocalDateTime.now())
                    .statisticType(StatisticType.BLOC_ATTENDANCE)
                    .description("Affluence bloc " + blocKey)
                    .unit("count")
                    .value(count.doubleValue())
                    .build();

            allStatsBloc.add(blocStat);
        }


        String mostPopular = countByBloc.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucune bloc");

        Statistics statistics = Statistics.builder()
                .key(mostPopular)
                .calculatedAt(LocalDateTime.now())
                .statisticType(StatisticType.TRIBUNE_ATTENDANCE)
                .description("bloc plus populaire"+mostPopular)
                .unit("name")
                .value(0.0)


        .build();

        return allStatsBloc;
    }

    private List<Statistics> calculateMatchAttendanceStatistics() {
        List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsMatch = new ArrayList<>();

        Map<String, Long> countByMatch = entrySpectateurs.stream()
                .filter(e -> e.getMatchId() != null)
                .collect(Collectors.groupingBy(
                        EntrySpectateur::getMatchId,
                        Collectors.counting()
                ));

        for (Map.Entry<String, Long> entry : countByMatch.entrySet()) {
            String matchId = entry.getKey();
            Long count = entry.getValue();

            Statistics matchStat = Statistics.builder()
                    .key(matchId)
                    .calculatedAt(LocalDateTime.now())
                    .statisticType(StatisticType.MATCH_ATTENDANCE)
                    .description("Nombre de spectateurs pour le match " + matchId)
                    .unit("count")
                    .value(count.doubleValue())
                    .matchId(matchId)
                    .build();

            allStatsMatch.add(matchStat);
        }

        return allStatsMatch;
    }

    private List<Statistics> calculateTop10ActiveSpectators() {
        List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsTop = new ArrayList<>();

        Map<String, Long> countBySpectator = entrySpectateurs.stream()
                .filter(e -> e.getSpectateur() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getSpectateur().getSpectatorId(),
                        Collectors.counting()
                ));

        List<Map.Entry<String, Long>> top10 = countBySpectator.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .toList();

        int rank = 1;
        for (Map.Entry<String, Long> entry : top10) {
            String spectatorId = entry.getKey();
            Long matchCount = entry.getValue();

            Statistics topStat = Statistics.builder()
                    .key("RANK_" + rank + "_" + spectatorId)
                    .calculatedAt(LocalDateTime.now())
                    .statisticType(StatisticType.TOP_ACTIVE_SPECTATORS)
                    .description("Rang " + rank + " : " + spectatorId + " avec " + matchCount + " matchs")
                    .unit("matches")
                    .value(matchCount.doubleValue())
                    .build();

            allStatsTop.add(topStat);
            rank++;
        }

        return allStatsTop;
    }

    private List<Statistics> calculateEntryTimeRangeStatistics() {
        List<EntrySpectateur> entrySpectateurs = entrySpectateurRepository.findAll();

        if (entrySpectateurs.isEmpty()) {
            return Collections.emptyList();
        }

        List<Statistics> allStatsTime = new ArrayList<>();

        Map<String, List<EntrySpectateur>> entriesByMatch = entrySpectateurs.stream()
                .filter(e -> e.getMatchId() != null)
                .filter(e -> e.getEntryTime() != null)
                .collect(Collectors.groupingBy(EntrySpectateur::getMatchId));

        for (Map.Entry<String, List<EntrySpectateur>> entry : entriesByMatch.entrySet()) {
            String matchId = entry.getKey();
            List<EntrySpectateur> entries = entry.getValue();

            Optional<LocalDateTime> minTime = entries.stream()
                    .map(EntrySpectateur::getEntryTime)
                    .min(LocalDateTime::compareTo);

            Optional<LocalDateTime> maxTime = entries.stream()
                    .map(EntrySpectateur::getEntryTime)
                    .max(LocalDateTime::compareTo);

            if (minTime.isPresent()) {
                Statistics minStat = Statistics.builder()
                        .key(matchId + "_MIN")
                        .calculatedAt(LocalDateTime.now())
                        .statisticType(StatisticType.ENTRY_TIME_MIN)
                        .description("Première entrée pour le match " + matchId + " : " + minTime.get())
                        .unit("time")
                        .value(minTime.get().getHour() * 60.0 + minTime.get().getMinute())
                        .matchId(matchId)
                        .build();

                allStatsTime.add(minStat);
            }

            if (maxTime.isPresent()) {
                Statistics maxStat = Statistics.builder()
                        .key(matchId + "_MAX")
                        .calculatedAt(LocalDateTime.now())
                        .statisticType(StatisticType.ENTRY_TIME_MAX)
                        .description("Dernière entrée pour le match " + matchId + " : " + maxTime.get())
                        .unit("time")
                        .value(maxTime.get().getHour() * 60.0 + maxTime.get().getMinute())
                        .matchId(matchId)
                        .build();

                allStatsTime.add(maxStat);
            }
        }

        return allStatsTime;
    }
}