package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.Confession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConfessionRepository extends JpaRepository<Confession, UUID> {
}