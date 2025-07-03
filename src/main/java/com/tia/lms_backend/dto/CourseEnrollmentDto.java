package com.tia.lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseEnrollmentDto {
    private UserDto user;
    private LocalDateTime startDate;
    private LocalDateTime deadlineDate;
    private LocalDateTime completionDate;
}
