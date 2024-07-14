package com.izzy.repository;

import com.izzy.model.UserRoleEntity;
import com.izzy.model.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRoleEntity, UserRoleKey> {
    @Override
    Optional<UserRoleEntity> findById(UserRoleKey userRoleKey);

}