package com.coresaken.jobportal.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    String secret;
    String amount;
    String serviceName;
    String websiteAddress;
    String orderId;
    String email;
    String personalData;
    String hash;
}
