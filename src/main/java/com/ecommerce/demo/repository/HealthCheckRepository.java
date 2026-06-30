package com.ecommerce.demo.repository;

import com.ecommerce.demo.entity.HealthCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HealthCheckRepository extends JpaRepository<HealthCheck, Long> {
}