package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.ContactDto;
import com.tia.lms_backend.dto.request.CreateContactRequest;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.model.enums.ContactPriority;
import com.tia.lms_backend.model.enums.ContactStatus;
import com.tia.lms_backend.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<GeneralResponse<ContactDto>> createContact(
            @RequestBody CreateContactRequest createContactRequest
    ) {
        ContactDto contactDto = contactService.create(createContactRequest);
        return ResponseEntity.ok(GeneralResponse.<ContactDto>builder()
                .code(201)
                .message("Contact created successfully.")
                .data(contactDto)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<ContactDto>> getContactById(@PathVariable UUID id) {
        ContactDto contactDto = contactService.getById(id);
        return ResponseEntity.ok(GeneralResponse.<ContactDto>builder()
                .code(200)
                .message("Contact retrieved successfully.")
                .data(contactDto)
                .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<ContactDto>>> getAll() {
        List<ContactDto> contacts = contactService.getAll();
        return ResponseEntity.ok(GeneralResponse.<List<ContactDto>>builder()
                .code(200)
                .message("Contacts retrieved successfully.")
                .data(contacts)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> deleteContact(@PathVariable UUID id) {
        contactService.delete(id);
        return ResponseEntity.ok(GeneralResponse.<Void>builder()
                .code(200)
                .message("Contact deleted successfully.")
                .build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<GeneralResponse<ContactDto>> updateStatus(
            @PathVariable UUID id,
            @RequestParam ContactStatus status
    ) {
        ContactDto updated = contactService.updateStatus(id, status);
        return ResponseEntity.ok(GeneralResponse.<ContactDto>builder()
                .code(200)
                .message("Contact status updated successfully.")
                .data(updated)
                .build());
    }
}
