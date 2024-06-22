package com.coresaken.jobportal.database.repository;

import com.coresaken.jobportal.database.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
}
