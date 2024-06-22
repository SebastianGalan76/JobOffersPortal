package com.coresaken.jobportal.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyResponse {
    String name;
    String logoUrl;
    String linkUrl;
}
