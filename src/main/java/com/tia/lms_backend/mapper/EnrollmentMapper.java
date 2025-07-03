package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.EnrollmentDto;
import com.tia.lms_backend.model.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    EnrollmentDto entityToDto(Enrollment savedEnrollment);
}
