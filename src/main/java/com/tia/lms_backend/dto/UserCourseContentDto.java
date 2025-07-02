package com.tia.lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.model.enums.Status;
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

    private Status status;


}
