package org.example.car_management_system.mapper;

import org.example.car_management_system.dto.response.CarResponse;
import org.example.car_management_system.model.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarMapper {

    @Mapping(source = "manufacture.name", target = "manufactureName.name")
    @Mapping(source = "manufacture.country", target = "manufactureName.country")
    @Mapping(source = "manufacture.id", target = "manufactureName.id")
    CarResponse toDto(Car car);

    List<CarResponse> toDtoList(List<Car> cars);
}
