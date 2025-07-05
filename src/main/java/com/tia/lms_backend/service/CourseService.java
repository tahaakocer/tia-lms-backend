package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.CourseDto;
import com.tia.lms_backend.dto.CourseEnrollmentDto;
import com.tia.lms_backend.dto.EnrollmentDto;
import com.tia.lms_backend.dto.request.CreateCourseRequest;
import com.tia.lms_backend.dto.request.CreateEmployeeRequest;
import com.tia.lms_backend.dto.response.CourseWithCompletionRateDto;
import com.tia.lms_backend.dto.response.CourseWithEnrollmentsDto;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.CourseMapper;
import com.tia.lms_backend.mapper.EnrollmentMapper;
import com.tia.lms_backend.model.*;
import com.tia.lms_backend.model.enums.Status;
import com.tia.lms_backend.repository.CourseCategoryRepository;
import com.tia.lms_backend.repository.CourseRepository;
import com.tia.lms_backend.repository.EnrollmentRepository;
import com.tia.lms_backend.repository.UserCourseContentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseMapper courseMapper;
    private final AwsS3Service awsS3Service;
    private final EnrollmentMapper enrollmentMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final UserCourseContentRepository userCourseContentRepository;
    public CourseService(CourseRepository courseRepository,
                         CourseCategoryRepository courseCategoryRepository,
                         CourseMapper courseMapper,
                         AwsS3Service awsS3Service,
                         EnrollmentMapper enrollmentMapper,
                         EnrollmentRepository enrollmentRepository,
                         UserCourseContentRepository userCourseContentRepository) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.courseMapper = courseMapper;
        this.awsS3Service = awsS3Service;
        this.enrollmentMapper = enrollmentMapper;
        this.enrollmentRepository = enrollmentRepository;
        this.userCourseContentRepository = userCourseContentRepository;
    }

    public CourseDto create(CreateCourseRequest request) {
        log.info("Creating course with request: {}", request);

        if (request == null || request.getName() == null || request.getName().isEmpty()) {
            throw new EntityNotFoundException("Course or course name cannot be null or empty");
        }
        if (courseRepository.existsByName(request.getName())) {
            throw new EntityAlreadyExistsException("Course with this name already exists");
        }

        CourseCategory category = courseCategoryRepository.findById(UUID.fromString(request.getCourseCategoryId()))
                .orElseThrow(() -> new EntityNotFoundException("Course category not found with id: " + request.getCourseCategoryId()));

        if (request.getCourseContents() == null || request.getCourseContents().isEmpty()) {
            log.error("Course contents cannot be empty");
            throw new EntityNotFoundException("Course contents cannot be empty");
        }

        Course course = Course.builder()
                .name(request.getName())
                .description(request.getDescription())
                .instructor(request.getInstructor())
                .durationMinutes(request.getDurationMinutes())
                .courseCategory(category)
                .mandatory(request.isMandatory())
                .build();

        List<CourseContent> courseContents = request.getCourseContents().stream()
                .map(contentDto -> CourseContent.builder()
                        .name(contentDto.getName())
                        .course(course)
                        .build())
                .collect(Collectors.toList());



        course.setCourseContents(courseContents);

        String imageUrl = uploadCourseImage(request.getName(), request.getImageFile());
        course.setImageUrl(imageUrl);

        Course savedCourse = saveEntity(course);
        log.info("Course created successfully: {}", savedCourse);

        return courseMapper.entityToDto(savedCourse);
    }

    private String uploadCourseImage(String name, MultipartFile file) {
        if(file == null || file.isEmpty()) {
            log.error("File is null or empty for course: {}", name);
            return "https://habitatbroward.org/wp-content/uploads/2020/01/10-Benefits-Showing-Why-Education-Is-Important-to-Our-Society.jpg";
        }
        else {
            log.info("Uploading course image for course: {}", name);
            return awsS3Service.uploadCourseImage(name, file);
        }
    }
    public CourseDto getById(UUID id) {
        log.info("Fetching course by id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        log.info("Course fetched successfully: {}", course);
        return courseMapper.entityToDto(course);
    }

    public CourseDto getByName(String name) {
        log.info("Fetching course by name: {}", name);
        Course course = courseRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with name: " + name));
        log.info("Course fetched successfully: {}", course);
        return courseMapper.entityToDto(course);
    }

    public List<CourseDto> getAll() {
        log.info("Fetching all courses");
        List<Course> courses = courseRepository.findAll();

        List<CourseDto> courseDtos = courses.stream()
                .map(courseMapper::entityToDto)
                .toList();
        log.info("Converted courses to DTOs: {}", courseDtos);
        return courseDtos;
    }
    public List<CourseWithCompletionRateDto> getAllWithCompletionRates() {
        log.info("Fetching all courses with completion rates");

        List<Course> courses = courseRepository.findAll();

        return courses.stream()
                .map(course -> {
                    List<UserCourseContent> contents = userCourseContentRepository.findAllByCourseId(course.getId());
                    long total = contents.size();
                    long completed = contents.stream().filter(c -> c.getStatus() == Status.COMPLETED).count();
                    double rate = (total == 0) ? 0.0 : ((double) completed / total) * 100.0;

                    return CourseWithCompletionRateDto.builder()
                            .course(courseMapper.entityToDto(course))
                            .completionRate(rate)
                            .build();
                })
                .collect(Collectors.toList());

    }

    public void delete(UUID id) {
        log.info("Deleting course with id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
        log.info("Course deleted successfully: {}", course);
    }

    public CourseWithEnrollmentsDto getByIdWithEnrollments(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        List<Enrollment> enrollments = enrollmentRepository.findAllByCourse(course);

        List<CourseEnrollmentDto> enrollmentDtos = enrollments.stream()
                .map(enrollmentMapper::entityToCourseEnrollmentDto)
                .toList();

        return CourseWithEnrollmentsDto.builder()
                .course(courseMapper.entityToDto(course))
                .enrollments(enrollmentDtos)
                .build();
    }
    private Course saveEntity(Course course) {
        try {
            if (course == null || course.getName() == null || course.getName().isEmpty()) {
                log.error("Course entity is null");
                throw new EntityNotFoundException("Course entity cannot be null");
            }
            if (courseRepository.existsByName(course.getName())) {
                log.error("Course with name {} already exists", course.getName());
                throw new EntityAlreadyExistsException("Course with this name already exists");
            }
            log.info("Saving course: {}", course.getName());
            return courseRepository.save(course);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error saving course: {}", course, e);
            throw new GeneralException("Error saving course", e);
        }
    }
}