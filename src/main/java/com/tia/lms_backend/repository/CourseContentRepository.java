package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseContentRepository extends JpaRepository<CourseContent, UUID> {
}
