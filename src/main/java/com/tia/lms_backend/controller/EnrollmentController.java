package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.EnrollmentDto;
import com.tia.lms_backend.dto.request.CreateEnrollmentRequest;
import com.tia.lms_backend.dto.response.EnrollmentResponseDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.model.Enrollment;
import com.tia.lms_backend.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<GeneralResponse<EnrollmentDto>> createEnrollment(@RequestBody CreateEnrollmentRequest request) {

        EnrollmentDto enrollmentDto = enrollmentService.createEnrollment(request);
        return ResponseEntity.ok(GeneralResponse.<EnrollmentDto>builder()
                .code(201)
                .message("Enrollment created successfully.")
                .data(enrollmentDto)
                .build());
    }

    @PostMapping("/user-course-content-status-to-complete")
    public ResponseEntity<GeneralResponse<?>> userCourseContentStatusToComplete (
            @RequestParam String userId,
            @RequestParam String courseContentId
    ) {
        this.enrollmentService.userCourseContentStatusToComplete(UUID.fromString(userId), UUID.fromString(courseContentId));
        return ResponseEntity.ok(GeneralResponse.builder()
                .code(200)
                .message("User course content status updated to complete successfully.")
                .build());
    }
    @PostMapping("/user-course-content-status-to-inprogress")
    public ResponseEntity<GeneralResponse<?>> userCourseContentStatusToInProgress (
            @RequestParam String userId,
            @RequestParam String courseContentId
    ) {
        this.enrollmentService.userCourseContentStatusToInProgress(UUID.fromString(userId), UUID.fromString(courseContentId));
        return ResponseEntity.ok(GeneralResponse.builder()
                .code(200)
                .message("User course content status updated to in-progress successfully.")
                .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<EnrollmentDto>>> getAllEnrollments() {
        List<EnrollmentDto> enrollmentDtoList = enrollmentService.getAll();
        return ResponseEntity.ok(GeneralResponse.<List<EnrollmentDto>>builder()
                .code(200)
                .message("Enrollment retrieved successfully.")
                .data(enrollmentDtoList)
                .build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<EnrollmentResponseDto>> getEnrollmentById(@PathVariable UUID id) {
        var enrollmentResponseDto = enrollmentService.getById(id);
        return ResponseEntity.ok(GeneralResponse.<EnrollmentResponseDto>builder()
                .code(200)
                .message("Enrollment retrieved successfully.")
                .data(enrollmentResponseDto)
                .build());
    }

    @GetMapping("/get-by-user-id")
    public ResponseEntity<GeneralResponse<List<EnrollmentResponseDto>>> getEnrollmentsByUserId(@RequestParam UUID userId) {
        List<EnrollmentResponseDto> enrollmentDtoList = enrollmentService.getByUserId(userId);
        return ResponseEntity.ok(GeneralResponse.<List<EnrollmentResponseDto>>builder()
                .code(200)
                .message("Enrollments retrieved successfully.")
                .data(enrollmentDtoList)
                .build());
    }

}
