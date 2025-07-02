package com.tia.lms_backend.controller;

import com.tia.lms_backend.dto.CourseCategoryDto;
import com.tia.lms_backend.dto.response.GeneralResponse;
import com.tia.lms_backend.service.CourseCategoryService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/course-categories")
@RequiredArgsConstructor
@Log4j2
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;

    @PostMapping
    public ResponseEntity<GeneralResponse<CourseCategoryDto>> create(
            @RequestParam @NotBlank String name,
            @RequestParam(required = false) String description) {

        log.info("Creating course category: {}", name);
        CourseCategoryDto dto = courseCategoryService.create(name, description);

        return ResponseEntity.ok(
                GeneralResponse.<CourseCategoryDto>builder()
                        .code(201)
                        .message("Created successfully")
                        .data(dto)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<GeneralResponse<List<CourseCategoryDto>>> getAll() {
        List<CourseCategoryDto> dtos = courseCategoryService.getAll();
        return ResponseEntity.ok(
                GeneralResponse.<List<CourseCategoryDto>>builder()
                        .code(200)
                        .message("Fetched all successfully")
                        .data(dtos)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<CourseCategoryDto>> getById(@PathVariable UUID id) {
        CourseCategoryDto dto = courseCategoryService.getById(id);
        return ResponseEntity.ok(
                GeneralResponse.<CourseCategoryDto>builder()
                        .code(200)
                        .message("Fetched by ID successfully")
                        .data(dto)
                        .build()
        );
    }
    @GetMapping("/get-by-name")
    public ResponseEntity<GeneralResponse<CourseCategoryDto>> getById(@RequestParam String name) {
        CourseCategoryDto dto = courseCategoryService.getByName(name);
        return ResponseEntity.ok(
                GeneralResponse.<CourseCategoryDto>builder()
                        .code(200)
                        .message("Fetched by name successfully")
                        .data(dto)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<Void>> delete(@PathVariable UUID id) {
        courseCategoryService.delete(id);
        return ResponseEntity.ok(
                GeneralResponse.<Void>builder()
                        .code(200)
                        .message("Deleted successfully")
                        .build()
        );
    }
}
