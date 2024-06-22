package com.coresaken.jobportal.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Salary {
    int id;
    String value;
    String employmentType;
}
