package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.dto.request.CreateEmployeeRequest;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

 ////   @PreAuthorize("hasAuthority(\"ROLE_HR\")")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse<UserDto>> createEmployee(
            @ModelAttribute CreateEmployeeRequest createEmployeeRequest
    ) {
        UserDto createdUser = employeeService.registerEmployee(createEmployeeRequest);
        return ResponseEntity.ok(GeneralResponse.<UserDto>builder()
                .code(201)
                .message("Employee created successfully.")
                .data(createdUser)
                .build());
    }
//    @PreAuthorize("hasAuthority(\"ROLE_HR\")")
    @GetMapping
    public ResponseEntity<GeneralResponse<List<UserDto>>> getAllEmployees() {
        List<UserDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(GeneralResponse.<List<UserDto>>builder()
                .code(200)
                .message("All employees fetched successfully.")
                .data(employees)
                .build());
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<UserDto>> getEmployeeById(@PathVariable UUID id) {
        UserDto employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(GeneralResponse.<UserDto>builder()
                .code(200)
                .message("Employee fetched successfully.")
                .data(employee)
                .build());
    }
  //  @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-role")
    public ResponseEntity<GeneralResponse<List<UserDto>>> getEmployeesByRole(@RequestParam String role) {
        List<UserDto> employees = employeeService.getEmployeesByRole(role);
        return ResponseEntity.ok(GeneralResponse.<List<UserDto>>builder()
                .code(200)
                .message("Employees by role fetched successfully.")
                .data(employees)
                .build());
    }
  //  @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-department/{departmentId}")
    public ResponseEntity<GeneralResponse<List<UserDto>>> getEmployeesByDepartment(@PathVariable UUID departmentId) {
        List<UserDto> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(GeneralResponse.<List<UserDto>>builder()
                .code(200)
                .message("Employees by department fetched successfully.")
                .data(employees)
                .build());
    }

   // @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-team/{teamId}")
    public ResponseEntity<GeneralResponse<List<UserDto>>> getEmployeesByTeam(@PathVariable UUID teamId) {
        List<UserDto> employees = employeeService.getEmployeesByTeamId(teamId);
        return ResponseEntity.ok(GeneralResponse.<List<UserDto>>builder()
                .code(200)
                .message("Employees by team fetched successfully.")
                .data(employees)
                .build());
    }
   // @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\")")
    @PostMapping("/assign-to-team")
    public ResponseEntity<GeneralResponse<UserDto>> assignToTeam(
            @RequestParam UUID userId,
            @RequestParam UUID teamId
    ) {
        UserDto assignedUser = employeeService.assignEmployeeToTeam(userId, teamId);
        return ResponseEntity.ok(GeneralResponse.<UserDto>builder()
                .code(200)
                .message("User assigned to team successfully.")
                .data(assignedUser)
                .build());
    }
   // @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\")")
    @PostMapping("/remove-from-team")
    public ResponseEntity<GeneralResponse<UserDto>> removeFromTeam(
            @RequestParam UUID userId
    ) {
        UserDto updatedUser = employeeService.removeEmployeeFromTeam(userId);
        return ResponseEntity.ok(GeneralResponse.<UserDto>builder()
                .code(200)
                .message("User removed from team successfully.")
                .data(updatedUser)
                .build());
    }

}
