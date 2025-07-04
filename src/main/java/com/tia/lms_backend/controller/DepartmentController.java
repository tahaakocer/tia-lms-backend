package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.DepartmentDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.DepartmentService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PreAuthorize("hasAuthority(\"ROLE_HR\")")
    @PostMapping
    public ResponseEntity<GeneralResponse<DepartmentDto>> createDepartment(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description
    ) {
        DepartmentDto departmentDto = this.departmentService.create(name, description);
        return ResponseEntity.ok(GeneralResponse.<DepartmentDto>builder()
                .code(201)
                .message("Department created successfully.")
                .data(departmentDto)
                .build());
    }
    @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<DepartmentDto>> getDepartmentById(
            @PathVariable UUID id
    ) {
        DepartmentDto departmentDto = this.departmentService.getById(id);
        return ResponseEntity.ok(GeneralResponse.<DepartmentDto>builder()
                .code(200)
                .message("Department retrieved successfully.")
                .data(departmentDto)
                .build());
    }
    @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-name")
    public ResponseEntity<GeneralResponse<DepartmentDto>> getDepartmentByName(
            @RequestParam String name
    ) {
        DepartmentDto departmentDto = this.departmentService.getByName(name);
        return ResponseEntity.ok(GeneralResponse.<DepartmentDto>builder()
                .code(200)
                .message("Department retrieved successfully.")
                .data(departmentDto)
                .build());
    }
    @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping
    public ResponseEntity<GeneralResponse<List<DepartmentDto>>> getAll() {
        List<DepartmentDto> departmentDtos = this.departmentService.getAll();
        return ResponseEntity.ok(GeneralResponse.<List<DepartmentDto>>builder()
                .code(200)
                .message("Departments retrieved successfully.")
                .data(departmentDtos)
                .build());
    }
}
