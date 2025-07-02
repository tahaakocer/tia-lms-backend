package com.tia.lms_backend.repository;


import com.tia.lms_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {
    boolean existsByEmail(String email);
    boolean existsByTckn(String tckn);

}
