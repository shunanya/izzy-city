package com.izzy.repository;

import com.izzy.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Override
    Optional<UserEntity> findById(Long aLong);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(u.phone_number LIKE %:phoneNumber%)")
    UserEntity findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM UserEntity u WHERE " +
            "(:firstName IS NULL OR u.first_name LIKE %:firstName%) AND " +
            "(:lastName IS NULL OR u.last_name LIKE %:lastName%) AND " +
            "(:phoneNumber IS NULL OR u.phone_number LIKE %:phoneNumber%) AND " +
            "(:gender IS NULL OR u.gender = :gender) AND " +
//            "(:zoneId IS NULL OR u.zone.id = :zoneId) AND " +
            "(:shift IS NULL OR u.shift = :shift)")
    List<UserEntity> findUsersByFilters(@Param("firstName") String firstName,
                                        @Param("lastName") String lastName,
                                        @Param("phoneNumber") String phoneNumber,
                                        @Param("gender") String gender,
//                                  @Param("zoneId") Long zoneId,
                                        @Param("shift") String shift);
}
