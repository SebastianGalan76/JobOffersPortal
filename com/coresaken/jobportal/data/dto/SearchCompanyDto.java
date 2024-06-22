package com.coresaken.jobportal.data.dto;

import com.coresaken.jobportal.database.model.City;
import lombok.Data;

import java.util.Set;

@Data
public class SearchCompanyDto {
    String name;
    Set<City> cities;

    int page = 0;
}
