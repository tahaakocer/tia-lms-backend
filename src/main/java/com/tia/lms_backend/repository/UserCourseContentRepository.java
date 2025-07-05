package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.UserCourseContent;
import com.tia.lms_backend.model.enums.Status;
import io.lettuce.core.dynamic.annotation.Param;
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
    @Query("""
        SELECT ucc FROM UserCourseContent ucc 
        WHERE ucc.courseContent.course.id = :courseId
    """)
    List<UserCourseContent> findAllByCourseId(@Param("courseId") UUID courseId);

    @Query("""
        SELECT COUNT(ucc)
        FROM UserCourseContent ucc
        WHERE ucc.user.id = :userId
          AND ucc.courseContent.course.id = :courseId
          AND ucc.status = :status
    """)
    long countCompletedByUserAndCourse(
            @Param("userId") UUID userId,
            @Param("courseId") UUID courseId,
            @Param("status") Status status
    );

}
