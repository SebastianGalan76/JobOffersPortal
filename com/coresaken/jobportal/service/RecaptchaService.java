package com.coresaken.jobportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {
    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    public boolean isCorrect(String recaptchaToken){
        final String uri = "https://www.google.com/recaptcha/api/siteverify?secret=" + recaptchaSecret + "&response=" + recaptchaToken;

        RestTemplate restTemplate = new RestTemplate();
        JsonNode response = restTemplate.postForObject(uri, null, JsonNode.class);

        return response != null && response.get("success").asBoolean();
    }
}
