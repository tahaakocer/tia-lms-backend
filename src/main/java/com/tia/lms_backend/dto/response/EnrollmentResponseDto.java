package com.tia.lms_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.dto.*;
import com.tia.lms_backend.model.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentResponseDto {
    private UUID id; // Enrollment ID
    private UserDto user;
    private EnrollmentCourseDto course;

    private LocalDateTime startDate;
    private LocalDateTime deadlineDate;
    private LocalDateTime completionDate;
    private Status status;

    private List<EnrollmentCourseContentDto> userCourseContents;
}