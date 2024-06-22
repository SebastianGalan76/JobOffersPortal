package com.coresaken.jobportal.data.dto;

import com.coresaken.jobportal.data.Salary;
import com.coresaken.jobportal.data.SimpleCompany;
import com.coresaken.jobportal.data.ApplicationAction;
import com.coresaken.jobportal.database.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageJobOfferDto {
    Long id;

    String title;
    String description;
    Long categoryId;
    Long companyId;
    String backgroundUrl;
    String tags;

    SimpleCompany simpleCompany;
    List<Salary> salary;

    String visibility;
    ApplicationAction action;
    int promotionTier;

    List<Long> selectedExperienceLevels;
    List<String> customExperienceLevels;

    List<Long> selectedEmploymentTypes;
    List<String> customEmploymentTypes;

    List<Long> selectedWorkTypes;
    List<String> customWorkTypes;

    List<Location> locations;
}
