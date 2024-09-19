package com.izzy.repository;

import com.izzy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query ("SELECT r FROM Role r WHERE" +
            "(:name IS NULL OR r.name ILIKE %:name%)")
    Optional<Role> findByName(@Nullable String name);

    Optional<Role> findRoleByName(@Param("name") @NonNull String name);
}