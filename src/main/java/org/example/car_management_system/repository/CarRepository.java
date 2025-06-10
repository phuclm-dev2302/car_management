package org.example.car_management_system.repository;

import org.example.car_management_system.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID>, QuerydslPredicateExecutor<Car> {
}
