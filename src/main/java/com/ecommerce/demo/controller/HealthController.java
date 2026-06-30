package com.ecommerce.demo.controller;

import com.ecommerce.demo.entity.HealthCheck;
import com.ecommerce.demo.repository.HealthCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final HealthCheckRepository healthCheckRepository;

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        HealthCheck check = healthCheckRepository.findById(1L)
                .orElse(HealthCheck.builder().id(1L).count(0L).build());

        check.setCount(check.getCount() + 1);
        check.setLastCheckedAt(LocalDateTime.now().toString());
        healthCheckRepository.save(check);

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "count", check.getCount(),
                "lastCheck", check.getLastCheckedAt()
        ));
    }
}