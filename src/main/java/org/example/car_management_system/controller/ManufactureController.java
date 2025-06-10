package org.example.car_management_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.example.car_management_system.dto.request.CreateManufactureRequest;
import org.example.car_management_system.dto.response.ManufactoryResponse;
import org.example.car_management_system.dto.response.ResponseData;
import org.example.car_management_system.service.ManufactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/manufactures")
public class ManufactureController {
    @Autowired
    private ManufactureService manufactureService;

    @GetMapping("")
    @Operation(summary = "get List Manufactory")
    public ResponseEntity<ResponseData<List<ManufactoryResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(manufactureService.getAll(page, size));
    }

    @PostMapping("")
    public  ResponseEntity<ManufactoryResponse> create(@RequestBody @Valid CreateManufactureRequest request){
        return ResponseEntity.ok(manufactureService.create(request));
    }
}
