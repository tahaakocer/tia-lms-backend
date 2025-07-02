package com.tia.lms_backend.dto;

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


}
