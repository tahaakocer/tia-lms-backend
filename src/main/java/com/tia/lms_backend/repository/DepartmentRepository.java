package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    Optional<Department> findByName(String name);

    boolean existsByName(String name);
}
