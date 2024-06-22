package com.coresaken.jobportal.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCompany {
    String name;
    String linkUrl;
    String logoUrl;
}
