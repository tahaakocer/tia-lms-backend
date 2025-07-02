package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.CourseCategoryDto;
import com.tia.lms_backend.model.CourseCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseCategoryMapper {
    CourseCategoryDto entityToDto(CourseCategory courseCategory);
    CourseCategory dtoToEntity(CourseCategoryDto courseCategoryDto);
}
