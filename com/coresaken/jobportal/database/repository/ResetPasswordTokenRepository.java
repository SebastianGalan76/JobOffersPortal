package com.coresaken.jobportal.database.repository;

import com.coresaken.jobportal.database.model.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    Optional<ResetPasswordToken> findByToken(String token);
    Optional<ResetPasswordToken> findByUserId(Long userId);
}
