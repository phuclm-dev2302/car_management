package org.example.car_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.car_management_system.enums.ModelEnums;

import java.util.UUID;

@Data
public class CreateCarRequest {

    @NotNull(message = "manufactureId cannot be null")
    private UUID manufactureId;

    @NotBlank(message = "name cannot be null")
    private String name;

    @NotNull(message = "model cannot be null")
    private ModelEnums model;

}
