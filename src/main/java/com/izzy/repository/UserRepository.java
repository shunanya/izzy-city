package com.izzy.repository;

import com.izzy.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

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
/*

    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR u.firstName ILIKE %:firstName%) AND " +
            "(:lastName IS NULL OR u.lastName ILIKE %:lastName%) AND " +
            "(:phoneNumber IS NULL OR u.phoneNumber ILIKE %:phoneNumber%) AND " +
            "(:gender IS NULL OR u.gender ILIKE %:gender%) AND " +
            "(:shift IS NULL OR u.shift ILIKE %:shift%) AND " +
            "(:zone IS NULL OR u.zone IS NULL OR u.zone.name ILIKE %:zone%)")
    List<User> findUsersByFilters(@Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  @Param("phoneNumber") String phoneNumber,
                                  @Param("gender") String gender,
                                  @Param("zone") String zone,
                                  @Param("shift") String shift);
*/
/*
    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR u.firstName ILIKE %:firstName%) AND " +
            "(:lastName IS NULL OR u.lastName ILIKE %:lastName%) AND " +
            "(:phoneNumber IS NULL OR u.phoneNumber ILIKE %:phoneNumber%) AND " +
            "(:gender IS NULL OR u.gender = :gender) AND " +
            "(:shift IS NULL OR u.shift = :shift) AND " +
            "(:zone IS NULL OR u.zone.name = :zone) AND " +
            "(:roles IS NULL OR EXISTS (SELECT r FROM u.roles r WHERE r.name IN :roles))")
    List<User> findUsersByFilters(@Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  @Param("phoneNumber") String phoneNumber,
                                  @Param("gender") String gender,
                                  @Param("zone") String zone,
                                  @Param("shift") String shift,
                                  @Param("roles") List<String> roles);
*/
}
