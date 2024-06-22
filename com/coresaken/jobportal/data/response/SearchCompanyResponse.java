package com.coresaken.jobportal.data.response;

import org.springframework.http.HttpStatus;

import java.util.List;

public class SearchCompanyResponse extends Response {
    public List<Company> companies;

    public SearchCompanyResponse(HttpStatus status, String message, List<Company> companies) {
        super(status, message);

        this.companies = companies;
    }

    public static class Company{
        public Long id;
        public String name;
        public String nameUrl;
        public String logoUrl;
        public String locations;

        public Company(Long id, String name, String nameUrl, String logoUrl, String locations){
            this.id = id;
            this.name = name;
            this.nameUrl = nameUrl;
            this.logoUrl = logoUrl;
            this.locations = locations;
        }
    }
}
