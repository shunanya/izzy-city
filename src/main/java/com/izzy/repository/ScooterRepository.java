package com.izzy.repository;

import com.izzy.model.ScooterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScooterRepository extends JpaRepository<ScooterEntity, Long> {
    @Override
    Optional<ScooterEntity> findById(Long aLong);
}