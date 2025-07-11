package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, UUID> {
    Optional<CourseCategory> findByName(String name);
    boolean existsByName(String name);
}
