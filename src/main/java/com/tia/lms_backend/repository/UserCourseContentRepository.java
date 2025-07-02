package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.UserCourseContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserCourseContentRepository extends JpaRepository<UserCourseContent, UUID> {
}
