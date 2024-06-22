package com.coresaken.jobportal.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    String SEKRET;
    String KWOTA;
    String STATUS;
    String ID_ZAMOWIENIA;
    String ID_PLATNOSCI;
    String SECURE;
    String HASH;
}
