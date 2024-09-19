package com.izzy.repository;

import com.izzy.model.Zone;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @Query("SELECT z FROM Zone z WHERE z.name = :name")
    Optional<Zone> findByName(@NotBlank String name);

    Optional<Zone> findZoneByName(@Param("name") @NonNull String name);
}