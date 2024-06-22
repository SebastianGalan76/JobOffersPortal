package com.coresaken.jobportal.data.dto;

import com.coresaken.jobportal.database.model.Link;
import com.coresaken.jobportal.database.model.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private String name;
    private String description;
    private String linkUrl;
    private String logoUrl;
    private String backgroundUrl;
    private List<Location> locations;
    private List<Link> links;
}
