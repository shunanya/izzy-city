package com.izzy.repository;

import com.izzy.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import com.izzy.model.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Override
    void deleteById(Long aLong);

    Optional<RefreshToken> findByUserId(Long id);

    Optional<RefreshToken> findByToken(String current_token);

    @Modifying
    int deleteByUser(User user);

}
