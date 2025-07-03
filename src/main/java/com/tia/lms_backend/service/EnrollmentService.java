package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.EnrollmentDto;
import com.tia.lms_backend.dto.request.CreateEnrollmentRequest;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.EnrollmentMapper;
import com.tia.lms_backend.model.*;
import com.tia.lms_backend.model.enums.Status;
import com.tia.lms_backend.repository.CourseRepository;
import com.tia.lms_backend.repository.EnrollmentRepository;
import com.tia.lms_backend.repository.UserCourseContentRepository;
import com.tia.lms_backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EnrollmentService {
    private final EnrollmentMapper enrollmentMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final UserCourseContentRepository userCourseContentRepository;

    public EnrollmentService(EnrollmentMapper enrollmentMapper,
                             EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository, CourseRepository courseRepository, UserCourseContentRepository userCourseContentRepository) {
        this.enrollmentMapper = enrollmentMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.userCourseContentRepository = userCourseContentRepository;
    }

    public EnrollmentDto createEnrollment(CreateEnrollmentRequest request) {
        log.info("Creating enrollment for user: {} in course: {}", request.getUserId(), request.getCourseId());

        Course course = courseRepository.findById(UUID.fromString(request.getCourseId()))
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.getCourseId()));

        User user = userRepository.findById(UUID.fromString(request.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            log.error("Enrollment already exists for user: {} in course: {}", request.getUserId(), request.getCourseId());
            throw new EntityAlreadyExistsException("Enrollment already exists for this user in the course");
        }

        boolean isCourseMandatory = course.isMandatory();

        if (isCourseMandatory && request.getDeadLineDate() == null) {
            throw new GeneralException("Mandatory course must have a deadline date");
        }

        if (!isCourseMandatory) {
            request.setDeadLineDate(null);
        }

        Status enrollmentStatus;
        if (request.getStartDate() != null && !request.getStartDate().isAfter(LocalDateTime.now())) {
            enrollmentStatus = Status.IN_PROGRESS;
        } else {
            enrollmentStatus = Status.ASSIGNED;
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .startDate(request.getStartDate())
                .deadlineDate(request.getDeadLineDate())
                .status(enrollmentStatus)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Enrollment created successfully: {}", savedEnrollment);

        List<CourseContent> courseContents = course.getCourseContents();
        if (courseContents == null || courseContents.isEmpty()) {
            log.warn("No course contents found for course: {}", course.getId());
        } else {
            List<UserCourseContent> userCourseContents = courseContents.stream()
                    .map(content -> UserCourseContent.builder()
                            .courseContent(content)
                            .user(user)
                            .status(enrollmentStatus) // İstersen IN_PROGRESS de olur, kuralına göre
                            .build())
                            .collect(Collectors.toList());

            userCourseContentRepository.saveAll(userCourseContents);
            log.info("Created UserCourseContent records for user: {}", user.getId());
        }

        return enrollmentMapper.entityToDto(savedEnrollment);
    }

    private Enrollment saveEntity(Enrollment enrollment) {
        try {
            if (enrollment == null) {
                log.error("enrollment entity is invalid");
                throw new EntityNotFoundException("enrollment entity is invalid");
            }

            log.info("Saving enrollment: {}", enrollment);
            return enrollmentRepository.save(enrollment);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error saving enrollment: {}", enrollment, e);
            throw new GeneralException("Error saving enrollment", e);
        }
    }
}
