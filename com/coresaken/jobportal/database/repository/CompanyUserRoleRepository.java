package com.coresaken.jobportal.database.repository;

import com.coresaken.jobportal.database.model.CompanyUserRole;
import com.coresaken.jobportal.database.model.key.CompanyUserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyUserRoleRepository extends JpaRepository<CompanyUserRole, CompanyUserRoleKey> {

    @Modifying
    @Query("DELETE FROM CompanyUserRole c WHERE c.company.id = :companyId AND c.user.id = :userId")
    void deleteRoleForCompany(@Param("companyId") Long companyId, @Param("userId") Long userId);
}
