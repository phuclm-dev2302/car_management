package org.example.car_management_system.repository;

import org.example.car_management_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> , QuerydslPredicateExecutor<User> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
