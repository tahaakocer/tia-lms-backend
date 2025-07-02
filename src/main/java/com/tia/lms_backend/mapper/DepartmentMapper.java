package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.DepartmentDto;
import com.tia.lms_backend.model.Department;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentDto entityToDto(Department department);
}
