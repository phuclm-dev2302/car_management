package org.example.car_management_system.service;

import org.example.car_management_system.dto.request.CreateCarRequest;
import org.example.car_management_system.dto.response.CarResponse;
import org.example.car_management_system.dto.response.ResponseData;
import org.example.car_management_system.enums.ModelEnums;

import java.time.LocalDate;
import java.util.List;

public interface CarService {
    ResponseData<List<CarResponse>> getAll(int page, int size);
    ResponseData<List<CarResponse>> search(int page, int size,String name, ModelEnums model, String manufactureName, LocalDate buyDate);
    CarResponse create(CreateCarRequest request);

}
