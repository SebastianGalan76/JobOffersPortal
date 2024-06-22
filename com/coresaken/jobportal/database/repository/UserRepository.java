package com.coresaken.jobportal.database.repository;

import com.coresaken.jobportal.database.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailOrLogin(String email, String login);

    Optional<User> findByEmail(String email);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_company_follow WHERE user_id = :userId AND company_id = :companyId", nativeQuery = true)
    void unfollowCompanyById(@Param("userId") Long userId, @Param("companyId") Long companyId);
}
