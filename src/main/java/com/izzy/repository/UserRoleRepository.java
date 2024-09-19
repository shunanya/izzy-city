package com.izzy.repository;

import com.izzy.model.UserRole;
import com.izzy.model.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleKey> {

    @Override
    Optional<UserRole> findById(@Param ("userRoleKey") @NonNull UserRoleKey userRoleKey);

}