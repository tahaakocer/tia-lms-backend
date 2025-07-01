package com.tia.lms_backend.model;

import com.tia.lms_backend.model.enums.ContactStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Table(name = "contacts")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Contact extends BaseEntity {

    private String userId;
    private String name;
    private String lastName;
    private String email;
    private String contentTitle;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private ContactStatus contactStatus;

}
