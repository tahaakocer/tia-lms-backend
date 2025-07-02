package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto entityToDto(User savedUser);

}
