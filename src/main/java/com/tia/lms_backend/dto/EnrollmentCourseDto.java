package com.tia.lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollmentCourseDto {
    private String name;
    private String instructor;
    private String imageUrl;
    private String description;
    private String durationMinutes;
    private boolean mandatory;

    private CourseCategoryDto courseCategory;
}
