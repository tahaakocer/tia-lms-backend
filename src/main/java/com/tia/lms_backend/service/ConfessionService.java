package com.tia.lms_backend.service;

import com.tia.lms_backend.repository.ConfessionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ConfessionService {
    private final ConfessionRepository confessionRepository;

    public ConfessionService(ConfessionRepository confessionRepository) {
        this.confessionRepository = confessionRepository;
    }
}
