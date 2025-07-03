package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.CourseDto;
import com.tia.lms_backend.dto.request.CreateCourseRequest;
import com.tia.lms_backend.dto.request.CreateEmployeeRequest;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.CourseMapper;
import com.tia.lms_backend.model.Course;
import com.tia.lms_backend.model.CourseCategory;
import com.tia.lms_backend.model.CourseContent;
import com.tia.lms_backend.repository.CourseCategoryRepository;
import com.tia.lms_backend.repository.CourseRepository;
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

    public CourseService(CourseRepository courseRepository,
                         CourseCategoryRepository courseCategoryRepository,
                         CourseMapper courseMapper,
                         AwsS3Service awsS3Service) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.courseMapper = courseMapper;
        this.awsS3Service = awsS3Service;
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
        return awsS3Service.uploadCourseImage(name, file);
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
        if (courses.isEmpty()) {
            log.warn("No courses found");
            throw new EntityNotFoundException("No courses found");
        }
        List<CourseDto> courseDtos = courses.stream()
                .map(courseMapper::entityToDto)
                .toList();
        log.info("Converted courses to DTOs: {}", courseDtos);
        return courseDtos;
    }

    public void delete(UUID id) {
        log.info("Deleting course with id: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
        courseRepository.delete(course);
        log.info("Course deleted successfully: {}", course);
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