package com.tia.lms_backend.service;


import com.tia.lms_backend.dto.ContactDto;
import com.tia.lms_backend.dto.request.ContactStatusRequest;
import com.tia.lms_backend.dto.request.CreateContactRequest;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.mapper.ContactMapper;
import com.tia.lms_backend.model.Contact;
import com.tia.lms_backend.model.enums.ContactPriority;
import com.tia.lms_backend.model.enums.ContactStatus;
import com.tia.lms_backend.repository.ContactRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import com.tia.lms_backend.mapper.ContactMapper;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class ContactService {
    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final EmailService emailService;

    public ContactService(ContactRepository contactRepository, ContactMapper contactMapper, EmailService emailService){
        this.contactRepository = contactRepository;
        this.contactMapper = contactMapper;
        this.emailService = emailService;
    }

    public ContactDto create(CreateContactRequest request) {
        log.info("Creating contact for userId: {}", request.getUserId());
        Contact contact = contactMapper.createRequestToEntity(request);
        contact.setContactStatus(ContactStatus.PENDING);
        Contact savedContact = contactRepository.save(contact);
        log.info("Contact created successfully: {}", savedContact);
        return contactMapper.entityToDto(savedContact);
    }

    public ContactDto getById(UUID id) {
        log.info("Fetching contact by id: {}", id);
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
        return contactMapper.entityToDto(contact);
    }
    public List<ContactDto> getByUserId(UUID userId) {
        log.info("Fetching contacts for userId: {}", userId);
        List<Contact> contacts = contactRepository.findByUserId(String.valueOf(userId));
        if (contacts.isEmpty()) {
            throw new EntityNotFoundException("No contacts found for user with id: " + userId);
        }
        return contacts.stream().map(contactMapper::entityToDto).toList();
    }

    public List<ContactDto> getAll() {
        log.info("Fetching all contacts");
        List<Contact> contacts = contactRepository.findAll();
        if (contacts.isEmpty()) {
            throw new EntityNotFoundException("No contacts found");
        }
        return contacts.stream().map(contactMapper::entityToDto).toList();
    }

    public void delete(UUID id) {
        log.info("Deleting contact with id: {}", id);
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
        contactRepository.delete(contact);
        log.info("Contact deleted successfully: {}", contact);
    }

    public ContactDto updateStatus(UUID id, ContactStatusRequest request){
        log.info("Updating contact status for id: {}", id);
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with id: " + id));
        contact.setContactStatus(request.getStatus());
        Contact updated = contactRepository.save(contact);
        // Send email to user
        emailService.sendContactResponseEmail(
                contact.getEmail(), // or look up user by userId if needed
                contact.getName(), // if you want to display name in mail like "Hello + {name}"
                contact.getContentTitle(), // or course name
                request.getStatus().name(),
                request.getNote()
        );
        return contactMapper.entityToDto(updated);
    }




}
