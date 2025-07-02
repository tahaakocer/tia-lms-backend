package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.TeamDto;
import com.tia.lms_backend.dto.TeamWithMembersDto;
import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.exception.EntityAlreadyExistsException;
import com.tia.lms_backend.exception.EntityNotFoundException;
import com.tia.lms_backend.exception.GeneralException;
import com.tia.lms_backend.mapper.TeamMapper;
import com.tia.lms_backend.mapper.UserMapper;
import com.tia.lms_backend.model.Department;
import com.tia.lms_backend.model.Team;
import com.tia.lms_backend.model.User;
import com.tia.lms_backend.repository.TeamRepository;
import com.tia.lms_backend.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;
    private final EmployeeService employeeService;
    private final KeycloakService keycloakService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public TeamService(TeamRepository teamRepository, TeamMapper teamMapper, EmployeeService employeeService, KeycloakService keycloakService, UserRepository userRepository, UserMapper userMapper) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;

        this.employeeService = employeeService;
        this.keycloakService = keycloakService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public TeamDto createTeam(String name, String leadId) {
        log.info("Creating team with name: {} and leadId: {}", name, leadId);
        if (teamRepository.existsByName(name)) {
            log.error("Team with name {} already exists", name);
            throw new EntityAlreadyExistsException("Team with this name already exists");
        }

        User user = employeeService.getUserById(UUID.fromString(leadId));
        if (user.getTeam() != null) {
            log.error("User with id {} is already assigned to a team", leadId);
            throw new EntityAlreadyExistsException("User is already assigned to a team");
        }

        log.info("User with id {} is valid and not assigned to any team", leadId);
       Team team = Team.builder()
                .name(name)
                .leadId(user.getId())
               .build();

        this.keycloakService.promoteEmployeeToTeamLead(user.getKeycloakId());
        Team savedTeam = saveEntity(team);
        User changedUser = this.employeeService.promoteToTeamLead(user.getId(), team);
        log.info("User with id {} has been changed to TEAMLEAD role", changedUser.getId());

        return this.teamMapper.entityToDto(savedTeam);
    }
    public TeamDto getByName(String name) {
        log.info("Fetching team by name: {}", name);
        Team team = teamRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with name: " + name));
        log.info("Team fetched successfully: {}", team);
        return teamMapper.entityToDto(team);
    }
    public TeamDto getById(UUID id) {
        log.info("Fetching team by id: {}", id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
        log.info("Team fetched successfully: {}", team);
        return teamMapper.entityToDto(team);
    }
    public TeamDto updateTeam(UUID id, String name, String leadId) {
        log.info("Updating team with id: {}, name: {}, leadId: {}", id, name, leadId);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));

        if (name != null && !name.isEmpty()) {
            team.setName(name);
        }
        if (leadId != null && !leadId.isEmpty()) {
            team.setLeadId(UUID.fromString(leadId));
        }

        Team updatedTeam = saveEntity(team);
        return teamMapper.entityToDto(updatedTeam);
    }
    private Team saveEntity(Team team) {
        try {
            if (team == null || team.getName() == null || team.getName().isEmpty()) {
                log.error("Team entity is null");
                throw new EntityNotFoundException("Team entity cannot be null");
            }
            if (teamRepository.existsByName(team.getName())) {
                log.error("Team with name {} already exists", team.getName());
                throw new EntityAlreadyExistsException("Team with this name already exists");
            }

            log.info("Saving Team: {}", team.getName());
            return teamRepository.save(team);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error saving team: {}", team, e);
            throw new GeneralException("Error saving team", e);
        }
    }

    public TeamDto getByLead(String leadId) {
    log.info("Fetching team by leadId: {}", leadId);
        Team team = teamRepository.findByLeadId(UUID.fromString(leadId))
                .orElseThrow(() -> new EntityNotFoundException("Team not found with leadId: " + leadId));
        log.info("Team fetched successfully: {}", team);
        return teamMapper.entityToDto(team);
    }

    public List<Team> getAll() {
        log.info("Fetching all teams");
        List<Team> teams = teamRepository.findAll();
        if (teams.isEmpty()) {
            log.warn("No teams found");
            throw new EntityNotFoundException("No teams found");
        }
        log.info("Teams fetched successfully: {}", teams);
        return teams;
    }
    public TeamWithMembersDto getTeamWithMembersById(UUID teamId) {
        log.info("Fetching team with members by id: {}", teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + teamId));

        List<User> members = userRepository.findAllByTeam(team);
        List<UserDto> memberDtos = members.stream()
                .map(this.userMapper::entityToDto)
                .toList();

        return TeamWithMembersDto.builder()
                .id(team.getId())
                .name(team.getName())
                .leadId(team.getLeadId())
                .members(memberDtos)
                .build();
    }
    public TeamWithMembersDto getTeamWithMembersByName(String name) {
        log.info("Fetching team with members by name: {}", name);

        Team team = teamRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with name: " + name));

        List<User> members = userRepository.findAllByTeam(team);
        List<UserDto> memberDtos = members.stream()
                .map(this.userMapper::entityToDto)
                .toList();

        return TeamWithMembersDto.builder()
                .id(team.getId())
                .name(team.getName())
                .leadId(team.getLeadId())
                .members(memberDtos)
                .build();
    }

}
