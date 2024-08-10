package com.izzy.repository;

import com.izzy.model.Scooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {
    @Override
    Optional<Scooter> findById(Long aLong);

    @Override
    boolean existsById(Long aLong);
}