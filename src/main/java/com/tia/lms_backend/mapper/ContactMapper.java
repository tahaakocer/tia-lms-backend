package com.tia.lms_backend.mapper;

import com.tia.lms_backend.dto.ContactDto;
import com.tia.lms_backend.dto.request.CreateContactRequest;
import com.tia.lms_backend.model.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactDto entityToDto(Contact contact);
    Contact dtoToEntity(ContactDto contactDto);

    @Mapping(target = "contactStatus", ignore = true) // Ignore contactStatus as it's set in service
    Contact createRequestToEntity(CreateContactRequest request);
}