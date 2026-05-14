package com.ecommerce.demo.controller;

import com.ecommerce.demo.entity.Tag;
import com.ecommerce.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    @GetMapping
    public List<Tag> getAll() {
        return tagRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Tag create(@RequestBody Tag tag) {
        if (tagRepository.existsByName(tag.getName())) {
            throw new RuntimeException("Tag already exists");
        }
        return tagRepository.save(tag);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Tag update(@PathVariable Long id, @RequestBody Tag data) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found"));
        tag.setName(data.getName());
        tag.setColor(data.getColor());
        return tagRepository.save(tag);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}