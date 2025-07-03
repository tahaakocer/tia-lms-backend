package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.CourseCategoryDto;
import com.tia.lms_backend.dto.CourseDto;
import com.tia.lms_backend.dto.EnrollmentCourseDto;
import com.tia.lms_backend.model.Course;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface CourseMapper {
    CourseDto entityToDto(Course course);
    Course dtoToEntity(CourseDto courseDto);

    EnrollmentCourseDto entityToEnrollmentCourseDto(Course course);
}