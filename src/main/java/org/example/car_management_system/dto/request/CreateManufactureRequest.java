package org.example.car_management_system.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateManufactureRequest {

    @NotBlank(message = "name not blank!")
    private String name;

    @NotBlank(message = "country not blank!")
    private String country;
}
