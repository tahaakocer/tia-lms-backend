package com.tia.lms_backend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.dto.CourseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseWithCompletionRateDto {
    private CourseDto course;
    private Double completionRate;
}
