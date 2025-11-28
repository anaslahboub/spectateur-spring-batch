package org.match.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.match.models.Statistics;


@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {

}