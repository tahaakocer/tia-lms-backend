package com.tia.lms_backend.repository;


import com.tia.lms_backend.model.Department;
import com.tia.lms_backend.model.Role;
import com.tia.lms_backend.model.Team;
import com.tia.lms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
    boolean existsByEmail(String email);
    boolean existsByTckn(String tckn);

    List<User> findAllByRole(Role role);

    List<User> findAllByDepartment(Department department);

    List<User> findAllByTeam(Team team);

    Optional<User> findByTckn(String tckn);
}
