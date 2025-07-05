package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.EnrollmentDto;
import com.tia.lms_backend.dto.request.CreateEnrollmentRequest;
import com.tia.lms_backend.dto.response.EnrollmentResponseDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.model.Enrollment;
import com.tia.lms_backend.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

 //   @PreAuthorize("hasAuthority(\"ROLE_HR\")")
    @PostMapping
    public ResponseEntity<GeneralResponse<EnrollmentDto>> createEnrollment(@RequestBody CreateEnrollmentRequest request) {

        EnrollmentDto enrollmentDto = enrollmentService.createEnrollment(request);
        return ResponseEntity.ok(GeneralResponse.<EnrollmentDto>builder()
                .code(201)
                .message("Enrollment created successfully.")
                .data(enrollmentDto)
                .build());
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
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
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
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

 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping
    public ResponseEntity<GeneralResponse<List<EnrollmentResponseDto>>> getAllEnrollments() {
        List<EnrollmentResponseDto> enrollmentDtoList = enrollmentService.getAll();
        return ResponseEntity.ok(GeneralResponse.<List<EnrollmentResponseDto>>builder()
                .code(200)
                .message("Enrollment retrieved successfully.")
                .data(enrollmentDtoList)
                .build());
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<EnrollmentResponseDto>> getEnrollmentById(@PathVariable UUID id) {
        var enrollmentResponseDto = enrollmentService.getById(id);
        return ResponseEntity.ok(GeneralResponse.<EnrollmentResponseDto>builder()
                .code(200)
                .message("Enrollment retrieved successfully.")
                .data(enrollmentResponseDto)
                .build());
    }

 ///   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-user-id/{userId}")
    public ResponseEntity<GeneralResponse<List<EnrollmentResponseDto>>> getEnrollmentsByUserId(@PathVariable UUID userId) {
        List<EnrollmentResponseDto> enrollmentDtoList = enrollmentService.getByUserId(userId);
        return ResponseEntity.ok(GeneralResponse.<List<EnrollmentResponseDto>>builder()
                .code(200)
                .message("Enrollments retrieved successfully.")
                .data(enrollmentDtoList)
                .build());
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-team-id/{teamId}")
    public ResponseEntity<GeneralResponse<List<EnrollmentResponseDto>>> getEnrollmentsByTeamId(@PathVariable UUID teamId) {
        List<EnrollmentResponseDto> responseList = enrollmentService.getByTeamId(teamId);
        return ResponseEntity.ok(
                GeneralResponse.<List<EnrollmentResponseDto>>builder()
                        .code(200)
                        .message("Enrollments retrieved successfully.")
                        .data(responseList)
                        .build()
        );
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-department-id/{departmentId}")
    public ResponseEntity<GeneralResponse<List<EnrollmentResponseDto>>> getEnrollmentsByDepartmentId(@PathVariable UUID departmentId) {
        List<EnrollmentResponseDto> responseList = enrollmentService.getByDepartmentId(departmentId);
        return ResponseEntity.ok(
                GeneralResponse.<List<EnrollmentResponseDto>>builder()
                        .code(200)
                        .message("Enrollments retrieved successfully.")
                        .data(responseList)
                        .build()
        );
    }

}
