package com.coresaken.jobportal.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    String firstName;
    String lastName;

    String login;
    String email;
    String password;
}
