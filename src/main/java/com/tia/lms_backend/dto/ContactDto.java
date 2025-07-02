package com.tia.lms_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tia.lms_backend.model.enums.ContactStatus;
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
public class ContactDto extends BaseDto {
    private String userId;
    private String name;
    private String lastName;
    private String email;
    private String contentTitle;
    private String content;
    private ContactStatus contactStatus;
}
