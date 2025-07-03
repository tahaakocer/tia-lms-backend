package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.EnrollmentCourseContentDto;
import com.tia.lms_backend.dto.EnrollmentDto;
import com.tia.lms_backend.dto.UserCourseContentDto;
import com.tia.lms_backend.dto.request.CreateEnrollmentRequest;
import com.tia.lms_backend.dto.response.EnrollmentResponseDto;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.CourseMapper;
import com.tia.lms_backend.mapper.EnrollmentMapper;
import com.tia.lms_backend.mapper.UserMapper;
import com.tia.lms_backend.model.*;
import com.tia.lms_backend.model.enums.Status;
import com.tia.lms_backend.repository.CourseRepository;
import com.tia.lms_backend.repository.EnrollmentRepository;
import com.tia.lms_backend.repository.UserCourseContentRepository;
import com.tia.lms_backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    public EnrollmentService(EnrollmentMapper enrollmentMapper,
                             EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository, CourseRepository courseRepository, UserCourseContentRepository userCourseContentRepository, UserMapper userMapper, CourseMapper courseMapper) {
        this.enrollmentMapper = enrollmentMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.userCourseContentRepository = userCourseContentRepository;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
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
                            .status(enrollmentStatus)
                            .build())
                    .collect(Collectors.toList());

            userCourseContentRepository.saveAll(userCourseContents);
            log.info("Created UserCourseContent records for user: {}", user.getId());
        }

        return enrollmentMapper.entityToDto(savedEnrollment);
    }

    @Transactional
    public void userCourseContentStatusToComplete(UUID userId, UUID userCourseContentId) {
        log.info("Updating UserCourseContent status to COMPLETED for user: {} and content: {}", userId, userCourseContentId);
        updateUserCourseContentStatus(userId, userCourseContentId, Status.COMPLETED);
    }

    @Transactional
    public void userCourseContentStatusToInProgress(UUID userId, UUID userCourseContentId) {
        log.info("Updating UserCourseContent status to IN_PROGRESS for user: {} and content: {}", userId, userCourseContentId);
        updateUserCourseContentStatus(userId, userCourseContentId, Status.IN_PROGRESS);
    }

    @Transactional
    protected void updateUserCourseContentStatus(UUID userId, UUID userCourseContentId, Status newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        UserCourseContent userCourseContent = userCourseContentRepository.findById(userCourseContentId)
                .orElseThrow(() -> new EntityNotFoundException("UserCourseContent not found with id: " + userCourseContentId));

        if (!userCourseContent.getUser().getId().equals(user.getId())) {
            throw new GeneralException("This UserCourseContent does not belong to the given user");
        }

        userCourseContent.setStatus(newStatus);
        userCourseContentRepository.save(userCourseContent);

        Course course = userCourseContent.getCourseContent().getCourse();

        Enrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Enrollment not found for user and course: userId=" + userId + ", courseId=" + course.getId()));

        List<UserCourseContent> allContents = userCourseContentRepository.findAllByUserAndCourse(user.getId(), course.getId());

        boolean allCompleted = allContents.stream().allMatch(ucc -> ucc.getStatus() == Status.COMPLETED);

        if (newStatus == Status.COMPLETED && allCompleted) {

            if (enrollment.getStatus() != Status.COMPLETED) {
                enrollment.setStatus(Status.COMPLETED);
                enrollment.setCompletionDate(LocalDateTime.now());
                enrollmentRepository.save(enrollment);
                log.info("All contents completed, enrollment marked as COMPLETED for user: {}", userId);
            }
        } else if (newStatus == Status.IN_PROGRESS && enrollment.getStatus() == Status.COMPLETED && !allCompleted) {
            enrollment.setStatus(Status.IN_PROGRESS);
            enrollment.setCompletionDate(null);
            enrollmentRepository.save(enrollment);
            log.info("A content was reopened, enrollment status reverted to IN_PROGRESS for user: {}", userId);
        }
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

    public List<EnrollmentDto> getAll() {
        log.info("Fetching all enrollments");
        List<Enrollment> enrollments;
        try {
            enrollments = enrollmentRepository.findAll();

        } catch (Exception e) {
            log.error("Error fetching enrollments", e);
            throw new GeneralException("Error fetching enrollments", e);
        }
        log.info("Enrollments fetched successfully: {}", enrollments);
        return enrollments.stream()
                .map(enrollmentMapper::entityToDto)
                .toList();
    }


    public List<EnrollmentResponseDto> getByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);

        return enrollments.stream().map(enrollment -> {
            List<UserCourseContent> userCourseContents = userCourseContentRepository
                    .findAllByUserAndCourse(userId, enrollment.getCourse().getId());


            List<EnrollmentCourseContentDto> enrollmentCourseContentDtos = userCourseContents.stream()
                    .map(enrollmentMapper::userCourseContentToEnrollmentCourseContentDto)
                    .toList();
            return EnrollmentResponseDto.builder()
                    .id(enrollment.getId())
                    .user(userMapper.entityToDto(enrollment.getUser()))
                    .course(courseMapper.entityToEnrollmentCourseDto(enrollment.getCourse()))
                    .startDate(enrollment.getStartDate())
                    .deadlineDate(enrollment.getDeadlineDate())
                    .completionDate(enrollment.getCompletionDate())
                    .status(enrollment.getStatus())
                    .userCourseContents(enrollmentCourseContentDtos)
                    .build();
        }).collect(Collectors.toList());
    }

    public EnrollmentResponseDto getById(UUID id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found with id: " + id));

        // UserCourseContents
        List<UserCourseContent> userCourseContents = userCourseContentRepository
                .findAllByUserAndCourse(enrollment.getUser().getId(), enrollment.getCourse().getId());

        List<EnrollmentCourseContentDto> enrollmentCourseContentDtos = userCourseContents.stream()
                .map(enrollmentMapper::userCourseContentToEnrollmentCourseContentDto)
                .toList();
        return EnrollmentResponseDto.builder()
                .id(enrollment.getId())
                .user(userMapper.entityToDto(enrollment.getUser()))
                .course(courseMapper.entityToEnrollmentCourseDto(enrollment.getCourse()))
                .startDate(enrollment.getStartDate())
                .deadlineDate(enrollment.getDeadlineDate())
                .completionDate(enrollment.getCompletionDate())
                .status(enrollment.getStatus())
                .userCourseContents(enrollmentCourseContentDtos)
                .build();
    }
}
