package com.coresaken.jobportal.data.dto;

import com.coresaken.jobportal.database.model.CompanyUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyUserRolesDto {
    CompanyUserRole.Role role;
    String userEmail;
}
