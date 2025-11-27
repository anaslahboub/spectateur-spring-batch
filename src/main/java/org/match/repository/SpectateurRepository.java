package org.match.repository;

import org.match.models.EntrySpectateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface  SpectateurRepository extends JpaRepository<EntrySpectateur, Long> {
    @Query("SELECT COUNT(s) FROM Spectateur s where s.spectatorId= :spectatorId")
    long countEntriesBySpectatorId(@Param("spectatorId") String spectatorId);

}
