package org.example.car_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.example.car_management_system.dto.request.CreateCarRequest;
import org.example.car_management_system.dto.response.CarResponse;
import org.example.car_management_system.dto.response.ResponseData;
import org.example.car_management_system.enums.ModelEnums;
import org.example.car_management_system.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/v1/cars")
@Validated
public class CarController {
    @Autowired
    private CarService carService;

    @Operation(summary = "Get all cars with pagination",
            description = "Returns a paginated list of cars. You can specify the page number and page size using query parameters.")
    @GetMapping("")
    public ResponseEntity<ResponseData<List<CarResponse>>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) @Max(50) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size) {
        return ResponseEntity.ok(carService.getAll(page, size));
    }

    @PostMapping("/query")
    public ResponseEntity<ResponseData<List<CarResponse>>> search(
            @RequestParam(defaultValue = "0") @Min(0) @Max(50) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ModelEnums model,
            @RequestParam(required = false) String manufactureName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate buyDate
    ) {
        return ResponseEntity.ok(carService.search(page,size,name, model, manufactureName, buyDate));
    }

    @PostMapping("")
    public CarResponse createCar(@RequestBody @Valid CreateCarRequest request){
        return carService.create(request);
    }

}
