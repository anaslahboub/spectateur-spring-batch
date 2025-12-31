package org.match.repository;

import org.match.models.Spectateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface  SpectateurRepository extends JpaRepository<Spectateur, String> {


}
