package com.izzy.repository;

import com.izzy.model.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    @Override
    Optional<RefreshTokenEntity> findById(Long aLong);
}
