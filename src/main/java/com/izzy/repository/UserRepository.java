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
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE " +
            "(u.phone_number LIKE %:phoneNumber%)")
    Optional<User> findByPhoneNumber(@NotBlank String phoneNumber);

    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR u.first_name LIKE %:firstName%) AND " +
            "(:lastName IS NULL OR u.last_name LIKE %:lastName%) AND " +
            "(:phoneNumber IS NULL OR u.phone_number LIKE %:phoneNumber%) AND " +
            "(:gender IS NULL OR u.gender = :gender) AND " +
//            "(:zoneId IS NULL OR u.zone.id = :zoneId) AND " +
            "(:shift IS NULL OR u.shift = :shift)")
    List<User> findUsersByFilters(@Param("firstName") String firstName,
                                  @Param("lastName") String lastName,
                                  @Param("phoneNumber") String phoneNumber,
                                  @Param("gender") String gender,
//                                  @Param("zoneId") Long zoneId,
                                  @Param("shift") String shift);
}
