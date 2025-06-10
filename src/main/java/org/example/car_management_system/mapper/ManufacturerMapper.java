package org.example.car_management_system.mapper;

import org.example.car_management_system.dto.request.CreateManufactureRequest;
import org.example.car_management_system.dto.response.ManufactoryResponse;
import org.example.car_management_system.model.Manufacture;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ManufacturerMapper {

    ManufactoryResponse toDto(Manufacture manufacture);
    Manufacture toEntity(CreateManufactureRequest manufactureRequest);
    List<ManufactoryResponse> toDtoList(List<Manufacture> manufactures);
}
