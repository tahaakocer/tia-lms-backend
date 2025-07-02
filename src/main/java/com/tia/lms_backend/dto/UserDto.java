package com.tia.lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.model.Department;
import com.tia.lms_backend.model.Role;
import com.tia.lms_backend.model.Team;
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
public class UserDto  extends BaseDto{
    private String keycloakId;
    private String avatarUrl;
    private String tckn;
    private String name;
    private String lastName;
    private String birthDate;
    private String email;
    private String education;
    private String title;

    private TeamDto team;


    private RoleDto role;

    private DepartmentDto department;

}
