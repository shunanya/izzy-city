package com.izzy.repository;

import com.izzy.model.Scooter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScooterRepository extends JpaRepository<Scooter, Long> {

    Optional<Scooter> findScooterByIdentifier(@Param("identifier") @NonNull String identifier);

    /**
     * finding Scooters with a battery level within a specified range.
     *
     * @param minBatteryLevel The minimum battery level (inclusive) to filter the scooters.
     * @param maxBatteryLevel The maximum battery level (inclusive) to filter the scooters
     * @return a list of Scooters whose battery levels fall within the specified range.
     */
    List<Scooter> findScootersByBatteryLevelBetween(@Param("minBatteryLevel") @NonNull Integer minBatteryLevel,
                                                    @Param("maxBatteryLevel") @NonNull Integer maxBatteryLevel);

    /**
     * finding Scooters with specified zone name
     *
     * @param zoneName the zone name where scooters are located
     * @return a list of Scooters which located in the specified zone
     */
    List<Scooter> findScootersByZone_Name(@Param("zoneName") String zoneName);

    @Query("SELECT s FROM Scooter s " +
            "JOIN s.zone z " +  // Join with the Zone entity
            "WHERE " +
            "(:identifier IS NULL OR s.identifier = :identifier) AND " +
            "(:minBatteryLevel IS Null OR :maxBatteryLevel IS NULL OR s.batteryLevel BETWEEN :minBatteryLevel AND :maxBatteryLevel) AND " +
            "(:minSpeedLimit IS NULL OR :maxSpeedLimit IS NULL OR s.speedLimit BETWEEN :minSpeedLimit AND :maxSpeedLimit) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:zoneName IS NULL OR z.name = :zoneName)")
    List<Scooter> findByFilter(@Param("identifier") @Nullable String identifier,
                               @Param("minBatteryLevel") @Nullable Integer minBatteryLevel, @Param("maxBatteryLevel") @Nullable Integer maxBatteryLevel,
                               @Param("minSpeedLimit") @Nullable Integer minSpeedLimit, @Param("maxSpeedLimit") @Nullable Integer maxSpeedLimit,
                               @Param("status") @Nullable String status,
                               @Param("zoneName") @Nullable String zoneName);

}