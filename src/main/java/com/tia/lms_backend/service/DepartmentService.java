package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.DepartmentDto;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.DepartmentMapper;
import com.tia.lms_backend.model.Department;
import com.tia.lms_backend.repository.DepartmentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public DepartmentDto create(String name, String description) {
        log.info("Creating department with name: {} and description: {}", name, description);
        Department department = Department.builder()
                .name(name)
                .description(description)
                .build();
        Department savedDepartment = saveEntity(department);
        log.info("Department created successfully: {}", savedDepartment);
        return departmentMapper.entityToDto(savedDepartment);
    }

    public DepartmentDto getById(UUID id) {
        log.info("Fetching department by id: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        log.info("Department fetched successfully: {}", department);
        return departmentMapper.entityToDto(department);
    }

    public DepartmentDto getByName(String name) {
        log.info("Fetching department by name: {}", name);
        Department department = departmentRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with name: " + name));
        log.info("Department fetched successfully: {}", department);
        return departmentMapper.entityToDto(department);
    }

    private Department saveEntity(Department department) {
        try {
            if (department == null || department.getName() == null || department.getName().isEmpty()) {
                log.error("Department entity is null");
                throw new EntityNotFoundException("Department entity cannot be null");
            }
            if (departmentRepository.existsByName(department.getName())) {
                log.error("Department with name {} already exists", department.getName());
                throw new EntityAlreadyExistsException("Department with this name already exists");
            }

            log.info("Saving department: {}", department.getName());
            return departmentRepository.save(department);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error saving department: {}", department, e);
            throw new GeneralException("Error saving department", e);
        }
    }

    public List<DepartmentDto> getAll() {
        log.info("Fetching all departments");
        List<Department> departments = departmentRepository.findAll();
        if (departments.isEmpty()) {
            log.warn("No departments found");
            throw new EntityNotFoundException("No departments found");
        }
        List<DepartmentDto> departmentDtos = departments.stream()
                .map(departmentMapper::entityToDto)
                .toList();
        log.info("Converted departments to DTOs: {}", departmentDtos);
        return departmentDtos;
    }
}
