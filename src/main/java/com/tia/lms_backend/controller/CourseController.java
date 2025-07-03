package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.CourseDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<GeneralResponse<CourseDto>> createCourse(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam String instructor,
            @RequestParam String durationMinutes,
            @RequestParam UUID courseCategoryId
    ) {
        CourseDto courseDto = courseService.create(name, description, instructor, durationMinutes, courseCategoryId);
        return ResponseEntity.ok(GeneralResponse.<CourseDto>builder()
                .code(201)
                .message("Course created successfully.")
                .data(courseDto)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<CourseDto>> getCourseById(@PathVariable UUID id) {
        CourseDto courseDto = courseService.getById(id);
        return ResponseEntity.ok(GeneralResponse.<CourseDto>builder()
                .code(200)
                .message("Course retrieved successfully.")
                .data(courseDto)
                .build());
    }

    @GetMapping("/get-by-name")
    public ResponseEntity<GeneralResponse<CourseDto>> getCourseByName(@RequestParam String name) {
        CourseDto courseDto = courseService.getByName(name);
        return ResponseEntity.ok(GeneralResponse.<CourseDto>builder()
                .code(200)
                .message("Course retrieved successfully.")
                .data(courseDto)
                .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<CourseDto>>> getAll() {
        List<CourseDto> courseDtos = courseService.getAll();
        return ResponseEntity.ok(GeneralResponse.<List<CourseDto>>builder()
                .code(200)
                .message("Courses retrieved successfully.")
                .data(courseDtos)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteCourse(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.ok(GeneralResponse.<Void>builder()
                .code(200)
                .message("Course deleted successfully.")
                .build());
    }
}