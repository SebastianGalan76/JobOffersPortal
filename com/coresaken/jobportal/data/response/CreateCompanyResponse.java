package com.coresaken.jobportal.data.response;

import org.springframework.http.HttpStatus;

public class CreateCompanyResponse extends Response{
    public String redirect;

    public CreateCompanyResponse(HttpStatus status, String message, String redirect) {
        super(status, message);
        this.redirect = redirect;
    }
}
