package org.match.repository;

import org.match.models.EntrySpectateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrySpectateurRepository extends JpaRepository<EntrySpectateur, Long> {

}
