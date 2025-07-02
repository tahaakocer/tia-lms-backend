package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.dto.request.CreateEmployeeRequest;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.UserMapper;
import com.tia.lms_backend.model.Department;
import com.tia.lms_backend.model.Role;
import com.tia.lms_backend.model.Team;
import com.tia.lms_backend.model.User;
import com.tia.lms_backend.repository.DepartmentRepository;
import com.tia.lms_backend.repository.RoleRepository;
import com.tia.lms_backend.repository.TeamRepository;
import com.tia.lms_backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class EmployeeService {
    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public EmployeeService(KeycloakService keycloakService,
                           UserRepository userRepository,
                           TeamRepository teamRepository,
                           DepartmentRepository departmentRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper) {
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }
    public UserDto registerEmployee(CreateEmployeeRequest request) {
        log.info("Registering employee with request: {}", request);
        if (request == null) {
            log.error("CreateEmployeeRequest is null");
            throw new EntityNotFoundException("CreateEmployeeRequest cannot be null");
        }

        Department department = departmentRepository.findById(UUID.fromString(request.getDepartmentId()))
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + request.getDepartmentId()));
        Role role = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: EMPLOYEE"));
        User user = User.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .tckn(request.getTckn())
                .birthDate(request.getBirthDate())
                .education(request.getEducation())
                .title(request.getTitle())
                .department(department)
                .role(role)
                .build();
        if(request.getTeamId() != null) {
            Team team = teamRepository.findById(UUID.fromString(request.getTeamId()))
                    .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + request.getTeamId()));
            user.setTeam(team);
        } else {
            user.setTeam(null);
        }
        String keycloakId = keycloakService.createKeycloakUser(user.getTckn(),user.getEmail(),null);
        user.setKeycloakId(keycloakId);
        User savedUser = saveEntity(user);
        return userMapper.entityToDto(savedUser);
    }


    private User saveEntity(User user) {
        try {
            if (user == null || user.getName() == null || user.getTckn().isEmpty()) {
                log.error("User entity is null");
                throw new EntityNotFoundException("User entity cannot be null");
            }
            if (userRepository.existsByTckn(user.getTckn())) {
                log.error("User with tckn {} already exists", user.getName());
                throw new EntityAlreadyExistsException("User with this tckn already exists");
            } else if (userRepository.existsByEmail(user.getEmail())) {
                log.error("User with email {} already exists", user.getEmail());
                throw new EntityAlreadyExistsException("User with this email already exists");
            }

            log.info("Saving User: {}", user.getName());
            return userRepository.save(user);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error saving user: {}", user, e);
            throw new GeneralException("Error saving user", e);
        }
    }
}
