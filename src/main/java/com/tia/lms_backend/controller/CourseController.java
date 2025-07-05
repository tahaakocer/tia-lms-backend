package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.CourseDto;
import com.tia.lms_backend.dto.request.CreateCourseRequest;
import com.tia.lms_backend.dto.response.CourseWithCompletionRateDto;
import com.tia.lms_backend.dto.response.CourseWithEnrollmentsDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

 //   @PreAuthorize("hasAuthority(\"ROLE_HR\")")
    @PostMapping
    public ResponseEntity<GeneralResponse<CourseDto>> createCourse(
            @ModelAttribute CreateCourseRequest createCourseRequest
    ) {
        CourseDto courseDto = courseService.create(createCourseRequest);
        return ResponseEntity.ok(GeneralResponse.<CourseDto>builder()
                .code(201)
                .message("Course created successfully.")
                .data(courseDto)
                .build());
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<CourseDto>> getCourseById(@PathVariable UUID id) {
        CourseDto courseDto = courseService.getById(id);
        return ResponseEntity.ok(GeneralResponse.<CourseDto>builder()
                .code(200)
                .message("Course retrieved successfully.")
                .data(courseDto)
                .build());
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-name")
    public ResponseEntity<GeneralResponse<CourseDto>> getCourseByName(@RequestParam String name) {
        CourseDto courseDto = courseService.getByName(name);
        return ResponseEntity.ok(GeneralResponse.<CourseDto>builder()
                .code(200)
                .message("Course retrieved successfully.")
                .data(courseDto)
                .build());
    }
  //  @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping
    public ResponseEntity<GeneralResponse<List<CourseWithCompletionRateDto>>> getAll() {
        List<CourseWithCompletionRateDto> courseDtos = courseService.getAllWithCompletionRates();
        return ResponseEntity.ok(GeneralResponse.<List<CourseWithCompletionRateDto>>builder()
                .code(200)
                .message("Courses retrieved successfully.")
                .data(courseDtos)
                .build());
    }
 //   @PreAuthorize("hasAuthority(\"ROLE_HR\")")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteCourse(@PathVariable UUID id) {
        courseService.delete(id);
        return ResponseEntity.ok(GeneralResponse.<Void>builder()
                .code(200)
                .message("Course deleted successfully.")
                .build());
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/{id}/with-enrollments")
    public ResponseEntity<GeneralResponse<CourseWithEnrollmentsDto>> getCourseWithEnrollments(@PathVariable UUID id) {
        CourseWithEnrollmentsDto dto = courseService.getByIdWithEnrollments(id);
        return ResponseEntity.ok(GeneralResponse.<CourseWithEnrollmentsDto>builder()
                .code(200)
                .message("Course with enrollments retrieved successfully.")
                .data(dto)
                .build());
    }

}