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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log4j2
public class EmployeeService {

    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AwsS3Service awsS3Service;

    public EmployeeService(KeycloakService keycloakService,
                           UserRepository userRepository,
                           TeamRepository teamRepository,
                           DepartmentRepository departmentRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper,
                           AwsS3Service awsS3Service) {
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.awsS3Service = awsS3Service;
    }

    public UserDto registerEmployee(CreateEmployeeRequest request) {
        log.info("Registering employee with request: {}", request);
        validateRequest(request);

        Department department = getDepartment(request.getDepartmentId());
        Role role = getEmployeeRole();

        Team team = getTeamIfPresent(request.getTeamId());

        User user = buildUser(request, department, role, team);

        user.setAvatarUrl(uploadProfilePicture(user.getTckn(), request));

        String keycloakId = keycloakService.createKeycloakUser(user.getTckn(), user.getEmail(), null);
        user.setKeycloakId(keycloakId);


        User savedUser = saveEntity(user);

        return userMapper.entityToDto(savedUser);
    }
    public UserDto registerEmployeeWithoutDefaultProfilePicture(CreateEmployeeRequest request) {
        log.info("Registering employee WITHOUT default profile picture. Request: {}", request);
        validateRequest(request);

        Department department = getDepartment(request.getDepartmentId());
        Role role = getEmployeeRole();

        User user = buildUser(request, department, role, null);

        // Fotoğraf yoksa NULL bırak
        user.setAvatarUrl(null);

        String keycloakId = keycloakService.createKeycloakUser(user.getTckn(), user.getEmail(), null);
        user.setKeycloakId(keycloakId);

        User savedUser = saveEntity(user);

        return userMapper.entityToDto(savedUser);
    }

    public List<UserDto> getAllEmployees() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(userMapper::entityToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all employees", e);
            throw new GeneralException("Error fetching all employees", e);
        }

    }
    public UserDto getEmployeeById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.entityToDto(user);
    }
    public List<UserDto> getEmployeesByRole(String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));

        List<User> users = userRepository.findAllByRole(role);
        return users.stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }
    public List<UserDto> getEmployeesByDepartmentId(UUID departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + departmentId));

        List<User> users = userRepository.findAllByDepartment(department);
        return users.stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }
    public List<UserDto> getEmployeesByTeamId(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));

        List<User> users = userRepository.findAllByTeam(team);
        return users.stream()
                .map(userMapper::entityToDto)
                .collect(Collectors.toList());
    }
    private void validateRequest(CreateEmployeeRequest request) {
        if (request == null) {
            log.error("CreateEmployeeRequest is null");
            throw new EntityNotFoundException("CreateEmployeeRequest cannot be null");
        }
    }

    private Department getDepartment(String departmentId) {
        return departmentRepository.findById(UUID.fromString(departmentId))
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + departmentId));
    }

    private Role getEmployeeRole() {
        return roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: EMPLOYEE"));
    }

    private Team getTeamIfPresent(String teamId) {
        if (teamId == null) return null;
        return teamRepository.findById(UUID.fromString(teamId))
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));
    }

    private String uploadProfilePicture(String tckn, CreateEmployeeRequest request) {
        if(request.getProfilePicture() == null) {
            log.warn("No profile picture provided for TCKN: {}", tckn);
            return "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png";
        }
        else {
            log.info("Uploading profile picture for TCKN: {}", tckn);
            return awsS3Service.uploadProfilePicture(tckn, request.getProfilePicture());

        }
    }

    private User buildUser(CreateEmployeeRequest request, Department department, Role role, Team team) {
        return User.builder()
                .name(request.getName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .tckn(request.getTckn())
                .birthDate(request.getBirthDate())
                .education(request.getEducation())
                .title(request.getTitle())
                .department(department)
                .role(role)
                .team(team)
                .build();
    }
    protected User promoteToTeamLead(UUID userId,Team team) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role newRole = roleRepository.findByName("TEAMLEAD")
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: TEAMLEAD"));

        user.setRole(newRole);
        user.setTeam(team);
        User updatedUser = saveEntity(user);

        log.info("User role changed successfully: {}", updatedUser);
        return updatedUser;

    }
    protected User changeRole(UUID userId, String newRoleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Role newRole = roleRepository.findByName(newRoleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: " + newRoleName));

        user.setRole(newRole);
        User updatedUser = saveEntity(user);

        log.info("User role changed successfully: {}", updatedUser);
        return updatedUser;
    }
    protected User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
    public UserDto assignEmployeeToTeam(UUID userId, UUID teamId) {
        log.info("Assigning user with id {} to team with id {}", userId, teamId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));

        if (user.getTeam() != null && user.getTeam().getId().equals(team.getId())) {
            log.error("User with id {} is already assigned to team with id {}", userId, teamId);
            throw new EntityAlreadyExistsException("User is already assigned to this team");
        }

        user.setTeam(team);

        Role employeeRole = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: EMPLOYEE"));
        user.setRole(employeeRole);

        User updatedUser = saveEntity(user);
        log.info("User with id {} successfully assigned to team with id {}", userId, teamId);

        return userMapper.entityToDto(updatedUser);
    }
    public UserDto removeEmployeeFromTeam(UUID userId) {
        log.info("Removing user with id {} from their team", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (user.getTeam() == null) {
            log.warn("User with id {} is not assigned to any team", userId);
            throw new EntityNotFoundException("User is not assigned to any team");
        }
        if(Objects.equals(user.getId(),user.getTeam().getLeadId())) {
            log.error("User with id {} is the team lead and cannot be removed from the team", userId);
            throw new GeneralException("Team lead cannot be removed from the team");
        }
        user.setTeam(null);

        Role employeeRole = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new EntityNotFoundException("Role not found with name: EMPLOYEE"));
        user.setRole(employeeRole);

        User updatedUser = saveEntity(user);
        log.info("User with id {} successfully removed from team", userId);

        return userMapper.entityToDto(updatedUser);
    }

    private User saveEntity(User user) {
        try {
            validateUser(user);

            if (user.getId() == null) { // SADECE yeni kayıt için kontrol et
                if (userRepository.existsByTckn(user.getTckn())) {
                    throw new EntityAlreadyExistsException("User with this tckn already exists");
                }
                if (userRepository.existsByEmail(user.getEmail())) {
                    throw new EntityAlreadyExistsException("User with this email already exists");
                }
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


    private void validateUser(User user) {
        if (user == null || user.getName() == null || user.getTckn() == null || user.getTckn().isEmpty()) {
            log.error("User entity is invalid");
            throw new EntityNotFoundException("User entity is invalid");
        }
    }

    public UserDto getUserByTckn(String tckn) {
        log.info("Fetching user by TCKN: {}", tckn);
        User user = userRepository.findByTckn(tckn)
                .orElseThrow(() -> new EntityNotFoundException("User not found with TCKN: " + tckn));
        return userMapper.entityToDto(user);
    }
    public UserDto updateEmployee(UUID userId, CreateEmployeeRequest request) {
        log.info("Updating employee with id: {} and request: {}", userId, request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getEducation() != null) user.setEducation(request.getEducation());
        if (request.getTitle() != null) user.setTitle(request.getTitle());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(UUID.fromString(request.getDepartmentId()))
                    .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + request.getDepartmentId()));
            user.setDepartment(department);
        }

        if (request.getTeamId() != null) {
            Team team = teamRepository.findById(UUID.fromString(request.getTeamId()))
                    .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + request.getTeamId()));
            user.setTeam(team);
        }

        if (request.getProfilePicture() != null) {
            String uploadedUrl = awsS3Service.uploadProfilePicture(user.getTckn(), request.getProfilePicture());
            user.setAvatarUrl(uploadedUrl);
        }


        User updatedUser = saveEntity(user);
        log.info("Employee updated successfully: {}", updatedUser);

        return userMapper.entityToDto(updatedUser);
    }

    public void deleteEmployee(UUID userId) {
        log.info("Deleting employee with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Keycloak kullanıcısını sil
        keycloakService.deleteKeycloakUser(user.getKeycloakId());

        // Kullanıcıyı veritabanından sil
        userRepository.delete(user);
        log.info("Employee deleted successfully: {}", user);
    }
}
