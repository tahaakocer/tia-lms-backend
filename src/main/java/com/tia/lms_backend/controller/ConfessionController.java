package com.tia.lms_backend.controller;

import com.tia.lms_backend.model.Confession;
import com.tia.lms_backend.repository.ConfessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/confessions")
@RequiredArgsConstructor
public class ConfessionController {

    private final ConfessionRepository confessionRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<Confession> createConfession(@RequestBody Confession confession) {
        Confession saved = confessionRepository.save(confession);
        return ResponseEntity.ok(saved);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Confession>> getAllConfessions() {
        List<Confession> list = confessionRepository.findAll();
        return ResponseEntity.ok(list);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Confession> getConfessionById(@PathVariable UUID id) {
        return confessionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Confession> updateConfession(@PathVariable UUID id, @RequestBody Confession update) {
        return confessionRepository.findById(id)
                .map(existing -> {
                    existing.setNickname(update.getNickname());
                    existing.setAge(update.getAge());
                    existing.setDepartment(update.getDepartment());
                    existing.setConfessionText(update.getConfessionText());
                    Confession saved = confessionRepository.save(existing);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfession(@PathVariable UUID id) {
        if (!confessionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        confessionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
