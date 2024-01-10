package com.pt.repository;

import com.pt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByAccount_Id(int accountId);

    Optional<RefreshToken> findByToken(String token);

    boolean existsByToken(String token);

}
