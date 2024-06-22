package com.coresaken.jobportal.database.repository.joboffer;

import com.coresaken.jobportal.database.model.joboffer.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
