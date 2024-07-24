package com.izzy.repository;

import com.izzy.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    List<User> findAll();

    @Override
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE " +
            "(u.phone_number LIKE %:phoneNumber%)")
    Optional<User> findByPhoneNumber(@NotBlank String phoneNumber);

    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR u.first_name ILIKE %:firstName%) AND " +
            "(:lastName IS NULL OR u.last_name ILIKE %:lastName%) AND " +
            "(:phoneNumber IS NULL OR u.phone_number ILIKE %:phoneNumber%) AND " +
            "(:gender IS NULL OR u.gender = :gender) AND " +
            "(:shift IS NULL OR u.shift = :shift) AND " +
            "(:zone IS NULL OR u.zone = :zone)")
    List<User> findUsersByFilters(@Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  @Param("phoneNumber") String phoneNumber,
                                  @Param("gender") String gender,
                                  @Param("zone") String zone,
                                  @Param("shift") String shift);
}
