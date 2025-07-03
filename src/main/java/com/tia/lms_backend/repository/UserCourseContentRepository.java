package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.UserCourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserCourseContentRepository extends JpaRepository<UserCourseContent, UUID> {
    @Query("""
    SELECT ucc FROM UserCourseContent ucc
    WHERE ucc.user.id = :userId AND ucc.courseContent.course.id = :courseId
""")
    List<UserCourseContent> findAllByUserAndCourse(UUID userId, UUID courseId);
}
