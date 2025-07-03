package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.CourseDto;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.CourseMapper;
import com.tia.lms_backend.model.Course;
import com.tia.lms_backend.model.CourseCategory;
import com.tia.lms_backend.repository.CourseCategoryRepository;
import com.tia.lms_backend.repository.CourseRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseMapper courseMapper;

    public CourseService(CourseRepository courseRepository, CourseCategoryRepository courseCategoryRepository, CourseMapper courseMapper) {
        this.courseRepository = courseRepository;
        this.courseCategoryRepository = courseCategoryRepository;
        this.courseMapper = courseMapper;
    }

    public CourseDto create(String name, String description, String instructor, String durationMinutes, UUID courseCategoryId) {
        log.info("Creating course with name: {}, instructor: {}, category: {}", name, instructor, courseCategoryId);
        if (name == null || name.isEmpty()) {
            throw new EntityNotFoundException("Course name cannot be null or empty");
        }
        if (courseRepository.existsByName(name)) {
            throw new EntityAlreadyExistsException("Course with this name already exists");
        }
        CourseCategory category = courseCategoryRepository.findById(courseCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("Course category not found with id: " + courseCategoryId));
        Course course = Course.builder()
                .name(name)
                .description(description)
                .instructor(instructor)
                .durationMinutes(durationMinutes)
                .courseCategory(category)
                .build();
        Course savedCourse = saveEntity(course);
        log.info("Course created successfully: {}", savedCourse);
        return courseMapper.entityToDto(savedCourse);
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