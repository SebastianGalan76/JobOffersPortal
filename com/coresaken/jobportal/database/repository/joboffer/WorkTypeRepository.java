package com.coresaken.jobportal.database.repository.joboffer;

import com.coresaken.jobportal.database.model.joboffer.EmploymentType;
import com.coresaken.jobportal.database.model.joboffer.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkTypeRepository extends JpaRepository<WorkType, Long> {

}
