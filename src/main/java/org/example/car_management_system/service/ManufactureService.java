package org.example.car_management_system.service;

import org.example.car_management_system.dto.request.CreateManufactureRequest;
import org.example.car_management_system.dto.response.ManufactoryResponse;
import org.example.car_management_system.dto.response.ResponseData;

import java.util.List;

public interface ManufactureService {
    ResponseData<List<ManufactoryResponse>> getAll(int page, int size);
    ManufactoryResponse create(CreateManufactureRequest request);
}

