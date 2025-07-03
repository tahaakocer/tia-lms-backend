package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    boolean existsByName(String name);
    Optional<Course> findByName(String name);
}