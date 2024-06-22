package com.coresaken.jobportal.data.response;

import org.springframework.http.HttpStatus;

public class CreateJobOfferResponse extends Response{
    public String redirect;

    public CreateJobOfferResponse(HttpStatus status, String message, String redirect) {
        super(status, message);
        this.redirect = redirect;
    }
}
