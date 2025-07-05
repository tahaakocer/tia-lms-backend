package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.UserDto;
import com.tia.lms_backend.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@Log4j2
public class ImportController {

    private final ImportService importService;

    @PostMapping("/employees")
    public ResponseEntity<List<UserDto>> importEmployees(@RequestParam("file") MultipartFile file) {
        log.info("Received employee Excel import request: {}", file.getOriginalFilename());
        List<UserDto> result = importService.importUsersFromExcel(file);
        return ResponseEntity.ok(result);
    }
}
