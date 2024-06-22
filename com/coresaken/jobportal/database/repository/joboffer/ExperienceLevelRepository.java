package com.coresaken.jobportal.database.repository.joboffer;

import com.coresaken.jobportal.database.model.joboffer.ExperienceLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExperienceLevelRepository extends JpaRepository<ExperienceLevel, Long> {

}
