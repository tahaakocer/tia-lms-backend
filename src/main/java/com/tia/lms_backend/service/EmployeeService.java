package com.tia.lms_backend.service;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.repository.UserRepository;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    private final Keycloak keycloak;
    private final UserRepository userRepository;

    public EmployeeService(Keycloak keycloak, UserRepository userRepository) {
        this.keycloak = keycloak;
        this.userRepository = userRepository;
    }
    public UserDto createEmployee(UserDto userDto) {

        return null;
    }
}
