package com.tia.lms_backend.controller;

import com.tia.lms_backend.service.AwsS3Service;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    private final AwsS3Service awsS3Service;

    public DemoController(AwsS3Service awsS3Service) {
        this.awsS3Service = awsS3Service;
    }

    @GetMapping
   // @PreAuthorize("hasAuthority('ROLE_HR')")
    public String demo() {
        return "Demo";
    }


}
