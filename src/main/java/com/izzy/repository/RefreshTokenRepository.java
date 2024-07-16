package com.izzy.repository;

import com.izzy.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.izzy.model.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Override
    void deleteById(Long aLong);

    Optional<RefreshToken> findByUserId(Long id);

    @Query("SELECT x FROM RefreshToken x WHERE " +
            "(x.current_token = :token)")
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);

}
