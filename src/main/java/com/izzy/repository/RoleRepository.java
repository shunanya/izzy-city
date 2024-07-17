package com.izzy.repository;

import com.izzy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Override
    Optional<Role> findById(Long id);

    @Query ("SELECT r FROM Role r WHERE" +
            "(:name IS NULL OR r.name ILIKE %:name%)")
    Optional<Role> findByName(String name);
}