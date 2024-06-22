package com.coresaken.jobportal.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelpMessageDto {
    String personalData;
    String email;
    String message;
    String recaptchaToken;
}
