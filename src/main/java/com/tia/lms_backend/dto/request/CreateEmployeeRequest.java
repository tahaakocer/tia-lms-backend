package com.tia.lms_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CreateEmployeeRequest {

    private String name;
    private String lastName;
    private String email;
    private String birthDate;
    private String tckn;

    private String education;
    private String title;

    private String teamId;
    private String departmentId;
}
