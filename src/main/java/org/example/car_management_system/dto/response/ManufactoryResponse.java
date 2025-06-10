package org.example.car_management_system.dto.response;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ManufactoryResponse {
    private UUID id;
    private String name;
    private String country;
}
