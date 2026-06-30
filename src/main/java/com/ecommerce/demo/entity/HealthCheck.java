package com.ecommerce.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "health_checks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthCheck {

    @Id
    private Long id = 1L;   // 永遠只有一筆

    private Long count = 0L;

    private String lastCheckedAt;
}