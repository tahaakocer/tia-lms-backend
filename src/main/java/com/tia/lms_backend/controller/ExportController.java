package com.tia.lms_backend.controller;

import com.tia.lms_backend.service.ExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExcelService excelService;

    @GetMapping("/users")
    public ResponseEntity<byte[]> exportUsers() throws Exception {
        byte[] excelBytes = excelService.exportUsersToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"users.xlsx\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/courses")
    public ResponseEntity<byte[]> exportCourses() throws Exception {
        byte[] excelBytes = excelService.exportCoursesToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"courses.xlsx\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }

    @GetMapping("/enrollments")
    public ResponseEntity<byte[]> exportEnrollmentsProgress() throws Exception {
        byte[] excelBytes = excelService.exportEnrollmentsWithProgressToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"enrollments-progress.xlsx\"");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}
