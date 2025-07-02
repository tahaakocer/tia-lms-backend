package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.RoleDto;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.RoleMapper;
import com.tia.lms_backend.model.Role;
import com.tia.lms_backend.repository.RoleRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleDto create(String name, String description) {
        log.info("Creating role with name: {} and description: {}", name, description);
       Role role = Role.builder()
                .name(name)
                .description(description)
                .build();
       Role savedRole = saveEntity(role);
        log.info("Role created successfully: {}", savedRole);
        return roleMapper.entityToDto(savedRole);
    }

    public RoleDto getById(UUID id) {
        log.info("Fetching role by id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + id));
        log.info("Role fetched successfully: {}", role);
        return roleMapper.entityToDto(role);
    }
    public RoleDto getByName(String name) {
        log.info("Fetching role by name: {}", name);
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + name));
        log.info("Role fetched successfully: {}", role);
        return roleMapper.entityToDto(role);
    }
    private Role saveEntity(Role role) {
        try {
            if (role == null || role.getName() == null || role.getName().isEmpty()) {
                log.error("Role entity is null");
                throw new EntityNotFoundException("Role entity cannot be null");
            }
            if (roleRepository.existsByName(role.getName())) {
                log.error("Role with name {} already exists", role.getName());
                throw new EntityAlreadyExistsException("Role with this name already exists");
            }

            log.info("Saving role: {}", role.getName());
            return roleRepository.save(role);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error saving role: {}", role, e);
            throw new GeneralException("Error saving role", e);
        }
    }

    public List<RoleDto> getAll() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            log.warn("No roles found");
            throw new EntityNotFoundException("No roles found");
        }
        List<RoleDto> roleDtos = roles.stream()
                .map(roleMapper::entityToDto)
                .toList();
        log.info("Converted roles to DTOs: {}", roleDtos);
        return roleDtos;
    }
}
