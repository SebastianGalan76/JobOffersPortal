package com.coresaken.jobportal.data.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobOfferPromotion{
    Long jobOfferId;
    int promotionTier;
}
