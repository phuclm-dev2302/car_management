package org.example.car_management_system.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.car_management_system.enums.ModelEnums;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Data
@Builder
public class CarResponse {
    private UUID id;
    private String name;
    private ModelEnums model;
    private LocalDate buyDate;
    private ManufactoryResponse manufactureName;


}
