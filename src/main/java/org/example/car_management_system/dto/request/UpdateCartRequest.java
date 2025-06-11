package org.example.car_management_system.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.car_management_system.enums.ModelEnums;

import java.util.UUID;
@Data
public class UpdateCartRequest {
    @NotNull(message = "manufactureId cannot be null")
    private UUID manufactureId;

    @NotBlank(message = "name cannot be null")
    private String name;

    @NotNull(message = "model cannot be null")
    @Enumerated(EnumType.STRING)
    private ModelEnums model;
}
