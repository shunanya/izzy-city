package com.izzy.repository;

import com.izzy.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.izzy.model.User;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserId(@NonNull Long id);

    @Query("SELECT x FROM RefreshToken x WHERE " +
            "(x.current_token = :token)")
    Optional<RefreshToken> findByToken(@NonNull String token);

    @Modifying
    int deleteByUser(@NonNull User user);

}
