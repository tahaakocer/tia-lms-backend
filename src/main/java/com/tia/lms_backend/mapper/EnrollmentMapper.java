package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.*;
import com.tia.lms_backend.model.CourseContent;
import com.tia.lms_backend.model.Enrollment;
import com.tia.lms_backend.model.UserCourseContent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    EnrollmentDto entityToDto(Enrollment savedEnrollment);

    UserCourseContentDto userCourseContentToDto(UserCourseContent ucc);

    EnrollmentCourseContentDto userCourseContentToEnrollmentCourseContentDto(UserCourseContent userCourseContent);

    CourseContentDto courseContentToDto(CourseContent content);

    CourseEnrollmentDto entityToCourseEnrollmentDto(Enrollment enrollment);
}
