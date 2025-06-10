package org.example.car_management_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.car_management_system.enums.ModelEnums;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cars")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacture_id", nullable = false)
    private Manufacture manufacture;

    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelEnums model;
    @Column(nullable = false)
    private LocalDate buyDate;
}
