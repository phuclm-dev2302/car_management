package org.example.car_management_system.service.Impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.car_management_system.config.exception.CarNotFoundException;
import org.example.car_management_system.config.exception.ManufactureNotFoundException;
import org.example.car_management_system.dto.request.CreateCarRequest;
import org.example.car_management_system.dto.response.CarResponse;
import org.example.car_management_system.dto.response.ResponseData;
import org.example.car_management_system.enums.ModelEnums;
import org.example.car_management_system.mapper.CarMapper;
import org.example.car_management_system.model.Car;
import org.example.car_management_system.model.Manufacture;
import org.example.car_management_system.model.QCar;
import org.example.car_management_system.repository.CarRepository;
import org.example.car_management_system.repository.ManufactureRepository;
import org.example.car_management_system.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarMapper carMapper;
    private final JPAQueryFactory queryFactory;
    @Autowired
    private ManufactureRepository manufactureRepository;
    @Autowired
    private CarRepository carRepository;

    @Override
    public ResponseData<List<CarResponse>> getAll(int page, int size) {
        QCar car = QCar.car;

        List<Car> result = queryFactory.selectFrom(car)
                .offset((long) page * size)
                .limit(size)
                .fetch();

        if (result.isEmpty()) {
            throw new CarNotFoundException("No cars found");
        }

        List<CarResponse> mapped = carMapper.toDtoList(result);

        return new ResponseData<>(200, "Cars retrieved successfully", mapped);
    }

    @Override
    public ResponseData<List<CarResponse>> search(int page,int size,String name, ModelEnums model, String manufactureName, LocalDate buyDate) {
        QCar car = QCar.car;

        BooleanExpression predicate = car.isNotNull();

        if (name != null && !name.isEmpty()) {
            predicate = predicate.and(car.name.containsIgnoreCase(name));
        }

        if (model != null) {
            predicate = predicate.and(car.model.eq(model));
        }

        if (manufactureName != null && !manufactureName.isEmpty()) {
            predicate = predicate.and(car.manufacture.name.containsIgnoreCase(manufactureName));
        }

        if (buyDate != null) {
            predicate = predicate.and(car.buyDate.eq(buyDate));
        }

        List<Car> cars = queryFactory
                .selectFrom(car)
                .offset((long) size*page)
                .leftJoin(car.manufacture).fetchJoin()
                .where(predicate)
                .limit(size)
                .fetch();

        if (cars.isEmpty()) {
            throw new CarNotFoundException("No cars matched search criteria");
        }

        List<CarResponse> responses = carMapper.toDtoList(cars);

        return new ResponseData<>(200, "Search completed", responses);
    }

    @Override
    public CarResponse create(CreateCarRequest request){
        Manufacture manufacture = manufactureRepository.findById(request.getManufactureId())
                .orElseThrow(() -> new ManufactureNotFoundException(
                        "Manufacture not found with id: " + request.getManufactureId()));
        Car car = Car.builder()
                .name(request.getName())
                .model(request.getModel())
                .buyDate(LocalDate.now())
                .manufacture(manufacture)
                .build();
        Car saved = carRepository.save(car);
        return carMapper.toDto(car);
    }
}
