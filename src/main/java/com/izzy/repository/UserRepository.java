package com.izzy.repository;

import com.izzy.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE " +
            "(u.phoneNumber LIKE %:phoneNumber%)")
    Optional<User> findByPhoneNumber(@NotBlank String phoneNumber);

    Optional<User> findUserByPhoneNumber(@Param("phoneNumber") @NonNull String phoneNumber);

    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR u.firstName ILIKE %:firstName%) AND " +
            "(:lastName IS NULL OR u.lastName ILIKE %:lastName%) AND " +
            "(:phoneNumber IS NULL OR u.phoneNumber ILIKE %:phoneNumber%) AND " +
            "(:gender IS NULL OR u.gender ILIKE %:gender%) AND " +
            "(:shift IS NULL OR u.shift ILIKE %:shift%)")
    List<User> findUsersByFilters(@Param("firstName") @Nullable String firstName,
                                  @Param("lastName") @Nullable String lastName,
                                  @Param("phoneNumber") @Nullable String phoneNumber,
                                  @Param("gender") @Nullable String gender,
                                  @Param("shift") @Nullable String shift);

    @Query("SELECT u FROM User u JOIN u.zone z WHERE " +
            "(:firstName IS NULL OR u.firstName = :firstName) AND " +
            "(:lastName IS NULL OR u.lastName = :lastName) AND " +
            "(:phoneNumber IS NULL OR u.phoneNumber = :phoneNumber) AND " +
            "(:gender IS NULL OR u.gender = :gender) AND " +
            "(CAST(:minDateOfBirth AS date) IS NULL OR CAST(u.dateOfBirth AS date) IS NULL OR u.dateOfBirth >= :minDateOfBirth) AND " +
            "(CAST(:maxDateOfBirth AS date) IS NULL OR CAST(u.dateOfBirth AS date) IS NULL OR u.dateOfBirth <= :maxDateOfBirth) AND " +
            "(:shift IS NULL OR u.shift = :shift) AND " +
            "(CAST(:minCreatedAt AS timestamp) IS NULL OR CAST(u.createdAt AS timestamp) IS NULL OR u.createdAt >= :minCreatedAt) AND " +
            "(CAST(:maxCreatedAt AS timestamp) IS NULL OR CAST(u.createdAt AS timestamp) IS NULL OR u.createdAt <= :maxCreatedAt) AND " +
            "(:zoneName IS NULL OR z.name = :zoneName)"
            )
    List<User> findUsersByFiltering(@Param("firstName") @Nullable String firstName,
                                    @Param("lastName") @Nullable String lastName,
                                    @Param("phoneNumber") @Nullable String phoneNumber,
                                    @Param("gender") @Nullable String gender,
                                    @Param("minDateOfBirth") @Nullable LocalDate minDateOfBirth, @Param("maxDateOfBirth") LocalDate maxDateOfBirth,
                                    @Param("shift") @Nullable String shift,
                                    @Param("minCreatedAt") @Nullable Timestamp minCreatedAt, @Param("maxCreatedAt") @Nullable Timestamp maxCreatedAt,
                                    @Param("zoneName") @Nullable String zoneName);

    @Query(value = "SELECT * FROM users u WHERE " +
            "(cast(:minCreatedAt AS timestamp) IS NULL OR u.created_at >= :minCreatedAt) AND " +
            "(cast(:maxCreatedAt AS timestamp) IS NULL OR u.created_at <= :maxCreatedAt)",
            nativeQuery = true)
    List<User> findUsersCreatedBetween(@Param("minCreatedAt") @Nullable Timestamp minCreatedAt, @Param("maxCreatedAt") @Nullable Timestamp maxCreatedAt);
}
