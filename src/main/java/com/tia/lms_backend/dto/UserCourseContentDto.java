package com.tia.lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.model.CourseContent;
import com.tia.lms_backend.model.User;
import com.tia.lms_backend.model.enums.Status;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCourseContentDto  extends BaseDto{
    private CourseContentDto courseContent;
    private UserDto user;
    private Status status;


}
