package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.TeamDto;
import com.tia.lms_backend.dto.TeamWithMembersDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.model.Team;
import com.tia.lms_backend.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

//    @PreAuthorize("hasAuthority(\"ROLE_HR\")")
    @PostMapping
    public ResponseEntity<GeneralResponse<TeamDto>> createTeam(
            @RequestParam String name, @RequestParam String leadId
    ) {
        TeamDto teamDto = teamService.createTeam(name, leadId);
        GeneralResponse<TeamDto> response = GeneralResponse.<TeamDto>builder()
                .code(201)
                .message("Team created successfully")
                .data(teamDto)
                .build();
        return ResponseEntity.status(201).body(response);

    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<TeamDto>> getTeamById(@PathVariable String id) {
        TeamDto teamDto = teamService.getById(UUID.fromString(id));
        GeneralResponse<TeamDto> response = GeneralResponse.<TeamDto>builder()
                .code(200)
                .message("Team fetched successfully")
                .data(teamDto)
                .build();
        return ResponseEntity.ok(response);
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-name")
    public ResponseEntity<GeneralResponse<TeamDto>> getTeamByName(@RequestParam String name) {
        TeamDto teamDto = teamService.getByName(name);
        GeneralResponse<TeamDto> response = GeneralResponse.<TeamDto>builder()
                .code(200)
                .message("Team fetched successfully")
                .data(teamDto)
                .build();
        return ResponseEntity.ok(response);
    }
//    @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/{id}/with-members")
    public ResponseEntity<GeneralResponse<TeamWithMembersDto>> getTeamWithMembersById(@PathVariable String id) {
        TeamWithMembersDto dto = teamService.getTeamWithMembersById(UUID.fromString(id));
        return ResponseEntity.ok(
                GeneralResponse.<TeamWithMembersDto>builder()
                        .code(200)
                        .message("Team with members fetched successfully")
                        .data(dto)
                        .build()
        );
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-name/with-members")
    public ResponseEntity<GeneralResponse<TeamWithMembersDto>> getTeamWithMembersByName(@RequestParam String name) {
        TeamWithMembersDto dto = teamService.getTeamWithMembersByName(name);
        return ResponseEntity.ok(
                GeneralResponse.<TeamWithMembersDto>builder()
                        .code(200)
                        .message("Team with members fetched successfully")
                        .data(dto)
                        .build()
        );
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping("/get-by-lead")
    public ResponseEntity<GeneralResponse<TeamDto>> getTeamByLead(@RequestParam String leadId) {
        TeamDto teamDto = teamService.getByLead(leadId);
        GeneralResponse<TeamDto> response = GeneralResponse.<TeamDto>builder()
                .code(200)
                .message("Team fetched successfully")
                .data(teamDto)
                .build();
        return ResponseEntity.ok(response);
    }
 //   @PreAuthorize("hasAnyAuthority(\"ROLE_HR\", \"ROLE_TEAMLEAD\", \"ROLE_EMPLOYEE\")")
    @GetMapping
    public ResponseEntity<GeneralResponse<List<Team>>> getAllTeams() {
        List<Team> teams = teamService.getAll();
        GeneralResponse<List<Team>> response = GeneralResponse.<List<Team>>builder()
                .code(200)
                .message("Teams fetched successfully")
                .data(teams)
                .build();
        return ResponseEntity.ok(response);
    }
}
