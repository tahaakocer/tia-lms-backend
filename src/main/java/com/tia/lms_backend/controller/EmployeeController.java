package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.dto.request.CreateEmployeeRequest;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse<UserDto>> createEmployee(
            @RequestBody CreateEmployeeRequest createEmployeeRequest
    ) {
        UserDto createdUser = employeeService.registerEmployee(createEmployeeRequest);
        return ResponseEntity.ok(GeneralResponse.<UserDto>builder()
                .code(201)
                .message("Employee created successfully.")
                .data(createdUser)
                .build());
    }
}
