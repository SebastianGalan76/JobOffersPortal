package com.coresaken.jobportal.database.repository;

import com.coresaken.jobportal.database.model.City;
import com.coresaken.jobportal.database.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByName(String name);
    Optional<Company> findByLinkUrl(String linkUrl);

    boolean existsByName(String name);
    boolean existsByLinkUrl(String linkUrl);

    @Query("SELECT c FROM Company c WHERE LOWER(c.name) LIKE :name%")
    Page<Company> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Company c JOIN c.locations l WHERE l.city IN :cities%")
    Page<Company> searchByCities(@Param("cities") Set<City> cities, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Company c JOIN c.locations l WHERE l.city IN :cities% AND LOWER(c.name) LIKE :name%")
    Page<Company> searchByCitiesAndName(@Param("cities") Set<City> cities, @Param("name") String name, Pageable pageable);
}
