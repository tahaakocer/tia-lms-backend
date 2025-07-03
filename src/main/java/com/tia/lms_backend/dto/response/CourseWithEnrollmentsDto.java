package com.tia.lms_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.dto.CourseDto;
import com.tia.lms_backend.dto.CourseEnrollmentDto;
import com.tia.lms_backend.dto.EnrollmentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseWithEnrollmentsDto {
    private CourseDto course;
    private List<CourseEnrollmentDto> enrollments;
}
