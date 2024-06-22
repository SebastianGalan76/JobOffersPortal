package com.coresaken.jobportal.data.dto;

import com.coresaken.jobportal.database.model.Company;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditCompanyDto {
    Company company;
    List<CompanyUserRolesDto> companyUserRolesDto;

    List<Long> removedLocationIds;
    List<Long> removedLinkIds;
}
