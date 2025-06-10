package org.example.car_management_system.repository;

import org.example.car_management_system.model.Manufacture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManufactureRepository extends JpaRepository<Manufacture, UUID>, QuerydslPredicateExecutor<Manufacture> {
}
