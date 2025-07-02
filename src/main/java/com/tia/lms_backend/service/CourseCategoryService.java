package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.CourseCategoryDto;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.CourseCategoryMapper;
import com.tia.lms_backend.model.CourseCategory;
import com.tia.lms_backend.repository.CourseCategoryRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class CourseCategoryService {

    private final CourseCategoryRepository courseCategoryRepository;
    private final CourseCategoryMapper courseCategoryMapper;

    public CourseCategoryService(CourseCategoryRepository courseCategoryRepository,
                                 CourseCategoryMapper courseCategoryMapper) {
        this.courseCategoryRepository = courseCategoryRepository;
        this.courseCategoryMapper = courseCategoryMapper;
    }

    public CourseCategoryDto create(String name, String description) {
        log.info("Creating course category with name: {} and description: {}", name, description);
        CourseCategory category = CourseCategory.builder()
                .name(name)
                .description(description)
                .build();
        CourseCategory savedCategory = saveEntity(category);
        log.info("Course category created successfully: {}", savedCategory);
        return courseCategoryMapper.entityToDto(savedCategory);
    }

    public CourseCategoryDto getById(UUID id) {
        log.info("Fetching course category by id: {}", id);
        CourseCategory category = courseCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course category not found with id: " + id));
        log.info("Course category fetched successfully: {}", category);
        return courseCategoryMapper.entityToDto(category);
    }

    public CourseCategoryDto getByName(String name) {
        log.info("Fetching course category by name: {}", name);
        CourseCategory category = courseCategoryRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Course category not found with name: " + name));
        log.info("Course category fetched successfully: {}", category);
        return courseCategoryMapper.entityToDto(category);
    }

    private CourseCategory saveEntity(CourseCategory category) {
        try {
            if (category == null || category.getName() == null || category.getName().isEmpty()) {
                log.error("Course category entity is null");
                throw new EntityNotFoundException("Course category entity cannot be null");
            }
            if (courseCategoryRepository.existsByName(category.getName())) {
                log.error("Course category with name {} already exists", category.getName());
                throw new EntityAlreadyExistsException("Course category with this name already exists");
            }

            log.info("Saving course category: {}", category.getName());
            return courseCategoryRepository.save(category);

        } catch (RuntimeException e) {
            throw e; // kontrollü hata ise aynen yukarı
        } catch (Exception e) {
            log.error("Error saving course category: {}", category, e);
            throw new GeneralException("Error saving course category", e);
        }
    }

    public void delete(UUID id) {
        log.info("Deleting course category with id: {}", id);
        CourseCategory category = courseCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course category not found with id: " + id));
        courseCategoryRepository.delete(category);
        log.info("Course category deleted successfully: {}", category);
    }

    public List<CourseCategoryDto> getAll() {
        log.info("Fetching all course categories");
        List<CourseCategory> categories = courseCategoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No course categories found");
            throw new EntityNotFoundException("No course categories found");
        }
        log.info("Course categories fetched successfully: {}", categories);
        List<CourseCategoryDto> categoryDtoList = categories.stream().map(courseCategoryMapper::entityToDto).toList();
        log.info("Converted course categories to DTOs: {}", categoryDtoList);

        return categoryDtoList;
    }
}
