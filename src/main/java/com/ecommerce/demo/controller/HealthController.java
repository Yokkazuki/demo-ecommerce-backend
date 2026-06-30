package com.ecommerce.demo.controller;

import com.ecommerce.demo.entity.HealthCheck;
import com.ecommerce.demo.repository.HealthCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final HealthCheckRepository healthCheckRepository;

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        HealthCheck check = healthCheckRepository.save(new HealthCheck());
        long count = healthCheckRepository.count();
        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "count", count,
                "lastCheck", check.getCheckedAt().toString()
        ));
    }
}