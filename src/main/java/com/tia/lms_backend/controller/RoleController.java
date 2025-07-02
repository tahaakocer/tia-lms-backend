package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.RoleDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;


    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<GeneralResponse<RoleDto>> createRole(
            @RequestParam String name,
            @RequestParam(required = false) String description
    ) {
        RoleDto roleDto = this.roleService.create(name, description);
        return ResponseEntity.ok(GeneralResponse.<RoleDto>builder()
                .code(201)
                .message("Role created successfully.")
                .data(roleDto)
                .build());
    }
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<RoleDto>> getRoleById(
            @RequestParam UUID id
    ) {
        RoleDto roleDto = this.roleService.getById(id);
        return ResponseEntity.ok(GeneralResponse.<RoleDto>builder()
                .code(200)
                .message("Role retrieved successfully.")
                .data(roleDto)
                .build());
    }
    @GetMapping("/get-by-name")
    public ResponseEntity<GeneralResponse<RoleDto>> getRoleByName(
            @RequestParam String name
    ) {
        RoleDto roleDto = this.roleService.getByName(name);
        return ResponseEntity.ok(GeneralResponse.<RoleDto>builder()
                .code(200)
                .message("Role retrieved successfully.")
                .data(roleDto)
                .build());
    }
    @GetMapping
    public ResponseEntity<GeneralResponse<List<RoleDto>>> getAll(
    ) {
        List<RoleDto> roleDtos = this.roleService.getAll();
        return ResponseEntity.ok(GeneralResponse.<List<RoleDto>>builder()
                .code(200)
                .message("Roles retrieved successfully.")
                .data(roleDtos)
                .build());
    }
}
