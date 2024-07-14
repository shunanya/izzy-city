package com.izzy.repository;

import com.izzy.model.ZoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<ZoneEntity, Long> {
    @Override
    Optional<ZoneEntity> findById(Long aLong);
}