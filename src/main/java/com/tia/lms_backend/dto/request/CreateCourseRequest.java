package com.tia.lms_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.dto.CourseContentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateCourseRequest {
    private String name;
    private String description;
    private String instructor;
    private String durationMinutes;
    private String courseCategoryId;
    private boolean mandatory;

    private List<CourseContentDto> courseContents;
    private MultipartFile imageFile;

}
