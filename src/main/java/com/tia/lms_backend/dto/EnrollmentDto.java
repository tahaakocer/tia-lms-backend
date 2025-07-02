package com.tia.lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.model.Course;
import com.tia.lms_backend.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentDto extends BaseDto{
    private UserDto user;

    private CourseDto course;
    private LocalDateTime startDate;
    private LocalDateTime deadlineDate;
    private LocalDateTime completionDate;



}
