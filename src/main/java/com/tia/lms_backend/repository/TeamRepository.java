package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    boolean existsByName(String name);
    boolean existsById(UUID id);

    Optional<Team> findByName(String name);

    Optional<Team> findByLeadId(UUID uuid);
}
