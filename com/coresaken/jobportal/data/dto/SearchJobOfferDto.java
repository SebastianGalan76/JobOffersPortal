package com.coresaken.jobportal.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchJobOfferDto {
    Set<Long> selectedCategories;
    Set<Long> selectedCities;
    Set<Long> selectedWorkTypes;
    Set<Long> selectedEmploymentTypes;
    Set<Long> selectedExperienceLevels;
    int page;
}
