package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.EnrollmentCourseContentDto;
import com.tia.lms_backend.dto.EnrollmentDto;
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
import com.tia.lms_backend.repository.*;
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
    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;

    public EnrollmentService(EnrollmentMapper enrollmentMapper,
                             EnrollmentRepository enrollmentRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository,
                             UserCourseContentRepository userCourseContentRepository,
                             UserMapper userMapper,
                             CourseMapper courseMapper,
                             TeamRepository teamRepository,
                             DepartmentRepository departmentRepository) {
        this.enrollmentMapper = enrollmentMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.userCourseContentRepository = userCourseContentRepository;
        this.userMapper = userMapper;
        this.courseMapper = courseMapper;
        this.teamRepository = teamRepository;
        this.departmentRepository = departmentRepository;
    }

    public EnrollmentDto createEnrollment(CreateEnrollmentRequest request) {
        log.info("Creating enrollment for user: {} in course: {}", request.getUserId(), request.getCourseId());

        Course course = courseRepository.findById(UUID.fromString(request.getCourseId()))
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + request.getCourseId()));
        User user = userRepository.findById(UUID.fromString(request.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new EntityAlreadyExistsException("Enrollment already exists for this user in the course");
        }

        if (course.isMandatory() && request.getDeadLineDate() == null) {
            throw new GeneralException("Mandatory course must have a deadline date");
        }

        if (!course.isMandatory()) {
            request.setDeadLineDate(null);
        }

        Status status = (request.getStartDate() != null && !request.getStartDate().isAfter(LocalDateTime.now()))
                ? Status.IN_PROGRESS : Status.ASSIGNED;

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .startDate(request.getStartDate())
                .deadlineDate(request.getDeadLineDate())
                .status(status)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);

        if (course.getCourseContents() != null && !course.getCourseContents().isEmpty()) {
            List<UserCourseContent> userCourseContents = course.getCourseContents().stream()
                    .map(content -> UserCourseContent.builder()
                            .courseContent(content)
                            .user(user)
                            .status(status)
                            .build())
                    .collect(Collectors.toList());
            userCourseContentRepository.saveAll(userCourseContents);
        }

        return enrollmentMapper.entityToDto(saved);
    }

    @Transactional
    public void userCourseContentStatusToComplete(UUID userId, UUID contentId) {
        updateUserCourseContentStatus(userId, contentId, Status.COMPLETED);
    }

    @Transactional
    public void userCourseContentStatusToInProgress(UUID userId, UUID contentId) {
        updateUserCourseContentStatus(userId, contentId, Status.IN_PROGRESS);
    }

    @Transactional
    protected void updateUserCourseContentStatus(UUID userId, UUID contentId, Status newStatus) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        UserCourseContent ucc = userCourseContentRepository.findById(contentId)
                .orElseThrow(() -> new EntityNotFoundException("UserCourseContent not found"));
        if (!ucc.getUser().getId().equals(userId)) {
            throw new GeneralException("UserCourseContent does not belong to this user");
        }

        ucc.setStatus(newStatus);
        userCourseContentRepository.save(ucc);

        Course course = ucc.getCourseContent().getCourse();
        Enrollment enrollment = enrollmentRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found for user and course"));

        List<UserCourseContent> allContents = userCourseContentRepository.findAllByUserAndCourse(userId, course.getId());
        boolean allCompleted = allContents.stream().allMatch(c -> c.getStatus() == Status.COMPLETED);

        if (newStatus == Status.COMPLETED && allCompleted) {
            if (enrollment.getStatus() != Status.COMPLETED) {
                enrollment.setStatus(Status.COMPLETED);
                enrollment.setCompletionDate(LocalDateTime.now());
                enrollmentRepository.save(enrollment);
            }
        } else if (newStatus == Status.IN_PROGRESS && enrollment.getStatus() == Status.COMPLETED && !allCompleted) {
            enrollment.setStatus(Status.IN_PROGRESS);
            enrollment.setCompletionDate(null);
            enrollmentRepository.save(enrollment);
        }
    }

    private EnrollmentResponseDto mapEnrollmentCourseContents(Enrollment enrollment, List<UserCourseContent> uccList, double totalRate) {
        long completed = uccList.stream().filter(u -> u.getStatus() == Status.COMPLETED).count();
        double courseRate = uccList.isEmpty() ? 0.0 : (double) completed / uccList.size() * 100.0;
        List<EnrollmentCourseContentDto> dtos = uccList.stream()
                .map(enrollmentMapper::userCourseContentToEnrollmentCourseContentDto)
                .collect(Collectors.toList());

        return EnrollmentResponseDto.builder()
                .id(enrollment.getId())
                .user(userMapper.entityToDto(enrollment.getUser()))
                .course(courseMapper.entityToEnrollmentCourseDto(enrollment.getCourse()))
                .startDate(enrollment.getStartDate())
                .deadlineDate(enrollment.getDeadlineDate())
                .completionDate(enrollment.getCompletionDate())
                .status(enrollment.getStatus())
                .userCourseContents(dtos)
                .courseCompletionRate(courseRate)
                .totalCompletionRate(totalRate)
                .build();
    }

    private List<EnrollmentResponseDto> resolveEnrollments(List<Enrollment> enrollments) {
        long totalContents = 0, totalCompleted = 0;

        List<List<UserCourseContent>> allContents = enrollments.stream()
                .map(e -> userCourseContentRepository.findAllByUserAndCourse(
                        e.getUser().getId(), e.getCourse().getId()))
                .toList();

        for (List<UserCourseContent> list : allContents) {
            totalContents += list.size();
            totalCompleted += list.stream().filter(u -> u.getStatus() == Status.COMPLETED).count();
        }

        double totalRate = totalContents == 0 ? 0.0 : (double) totalCompleted / totalContents * 100.0;

        return enrollments.stream().map(e -> {
            List<UserCourseContent> ucc = userCourseContentRepository.findAllByUserAndCourse(
                    e.getUser().getId(), e.getCourse().getId());
            return mapEnrollmentCourseContents(e, ucc, totalRate);
        }).toList();
    }

    public List<EnrollmentResponseDto> getAll() {
        return resolveEnrollments(enrollmentRepository.findAll());
    }

    public EnrollmentResponseDto getById(UUID id) {
        Enrollment e = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Enrollment not found"));
        List<UserCourseContent> ucc = userCourseContentRepository.findAllByUserAndCourse(
                e.getUser().getId(), e.getCourse().getId());
        long completed = ucc.stream().filter(u -> u.getStatus() == Status.COMPLETED).count();
        double total = ucc.isEmpty() ? 0.0 : (double) completed / ucc.size() * 100.0;
        return mapEnrollmentCourseContents(e, ucc, total);
    }

    public List<EnrollmentResponseDto> getByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return resolveEnrollments(enrollmentRepository.findByUser(user));
    }

    public List<EnrollmentResponseDto> getByTeamId(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found"));
        List<User> users = userRepository.findAllByTeam(team);
        return resolveEnrollments(enrollmentRepository.findAllByUserIn(users));
    }

    public List<EnrollmentResponseDto> getByDepartmentId(UUID departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found"));
        List<User> users = userRepository.findAllByDepartment(department);
        return resolveEnrollments(enrollmentRepository.findAllByUserIn(users));
    }
}
