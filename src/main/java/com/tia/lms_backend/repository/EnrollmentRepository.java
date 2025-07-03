package com.tia.lms_backend.repository;

import com.tia.lms_backend.model.Course;
import com.tia.lms_backend.model.Enrollment;
import com.tia.lms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    boolean existsByUserAndCourse(User user, Course course);

    Optional<Enrollment> findByUserAndCourse(User user, Course course);

    List<Enrollment> findByUser(User user);

    List<Enrollment> findAllByUserIn(List<User> usersInTeam);
    List<Enrollment> findAllByCourse(Course course);

}
