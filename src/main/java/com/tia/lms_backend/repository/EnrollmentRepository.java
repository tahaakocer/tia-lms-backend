package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.Course;
import com.tia.lms_backend.model.Enrollment;
import com.tia.lms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByUserAndCourse(User user, Course course);
}
