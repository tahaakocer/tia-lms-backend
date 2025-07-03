package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.EnrollmentDto;
import com.tia.lms_backend.dto.request.CreateEnrollmentRequest;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.model.Enrollment;
import com.tia.lms_backend.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
