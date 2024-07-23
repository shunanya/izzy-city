package com.izzy.repository;

import com.izzy.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    @Override
    Optional<Zone> findById(Long id);

    @Query("SELECT z FROM Zone z WHERE z.name = :name")
    Optional<Zone> findByName(String name);
}