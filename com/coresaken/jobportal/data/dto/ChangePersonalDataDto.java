package com.coresaken.jobportal.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePersonalDataDto {
    String firstName;
    String lastName;
}
