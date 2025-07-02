package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.RoleDto;
import com.tia.lms_backend.model.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto entityToDto(Role savedRole);
}
