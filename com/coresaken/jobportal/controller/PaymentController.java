package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.data.dto.PaymentDto;
import com.coresaken.jobportal.data.payment.JobOfferPromotion;
import com.coresaken.jobportal.data.payment.PaymentAction;
import com.coresaken.jobportal.database.model.Payment;
import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.coresaken.jobportal.database.repository.PaymentRepository;
import com.coresaken.jobportal.database.repository.joboffer.JobOfferRepository;
import com.coresaken.jobportal.service.PaymentService;
import com.coresaken.jobportal.service.joboffer.JobOfferService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {
    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    JobOfferService jobOfferService;

    @Value("${hotpay.secret}")
    String secret;
    @Value("${hotpay.password}")
    String password;
    @Value("${website.address}")
    String websiteAddress;

    @RequestMapping("/jobOffer/payment/{linkUrl}/{promotionTier}")
    public String getPaymentPage(@PathVariable("linkUrl") String linkUrl,
                                 @PathVariable("promotionTier") int promotionTier,
                                 Model model) {
        JobOffer jobOffer = jobOfferService.getJobOfferByLinkUrl(linkUrl);
        if(jobOffer == null){
            return "error-404";
        }

        JobOfferPromotion jobOfferPromotion = new JobOfferPromotion();
        jobOfferPromotion.setJobOfferId(jobOffer.getId());
        jobOfferPromotion.setPromotionTier(promotionTier);
        String successAction;
        try{
            ObjectMapper mapper = new ObjectMapper();
            successAction = mapper.writeValueAsString(jobOfferPromotion);
        }catch (JsonProcessingException e) {
            return "error-404";
        }

        Payment payment = new Payment();
        payment.setAction(PaymentAction.PROMOTE_JOB_OFFER);
        payment.setServiceId(paymentService.generateRandomServiceId());
        payment.setStatus(Payment.Status.DEFAULT);
        payment.setSuccessAction(successAction);
        payment = paymentRepository.save(payment);

        PaymentDto paymentDto = getPromotionPaymentDto(jobOffer.getTitle(), payment.getId());
        model.addAttribute("paymentRequest", paymentDto);

        return "subPage/payment";
    }


    @ResponseBody
    @PostMapping("/payment-notification")
    public String getPaymentNotification(
            @RequestParam(required = false) String ID_ZAMOWIENIA,
            @RequestParam(required = false) String KWOTA,
            @RequestParam(required = false) String ID_PLATNOSCI,
            @RequestParam(required = false) String STATUS,
            @RequestParam(required = false) String SECURE,
            @RequestParam(required = false) String SEKRET,
            @RequestParam(required = false) String HASH) {

        // Obsługa parametrów
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(password).append(";");
        if (KWOTA != null) {
            stringBuilder.append(KWOTA).append(";");
        }
        if (ID_PLATNOSCI != null) {
            stringBuilder.append(ID_PLATNOSCI).append(";");
        }
        if (ID_ZAMOWIENIA != null) {
            stringBuilder.append(ID_ZAMOWIENIA).append(";");
        }
        if (STATUS != null) {
            stringBuilder.append(STATUS).append(";");
        }
        if (SECURE != null) {
            stringBuilder.append(SECURE).append(";");
        }
        if (SEKRET != null) {
            stringBuilder.append(SEKRET);
        }

        String computedHash = paymentService.hashSHA256(stringBuilder.toString());

        if (HASH == null || !HASH.equals(computedHash)) {
            return "Hashes do not match!";
        }

        Payment payment = paymentRepository.findById(Long.parseLong(ID_ZAMOWIENIA)).orElse(null);
        if(payment == null){
            return "Wystąpił nieoczekiwany błąd!";
        }
        payment.setPaymentId(ID_PLATNOSCI);

        Payment.Status status = Payment.Status.valueOf(STATUS);
        payment.setStatus(status);
        paymentRepository.save(payment);

        if(status!= Payment.Status.SUCCESS){
            return STATUS;
        }

        if(payment.getAction() == PaymentAction.PROMOTE_JOB_OFFER){
            try{
                ObjectMapper mapper = new ObjectMapper();
                JobOfferPromotion jobOfferPromotion = mapper.readValue(payment.getSuccessAction(), JobOfferPromotion.class);

                jobOfferService.startPromotion(jobOfferPromotion);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return STATUS;
    }



    private PaymentDto getPromotionPaymentDto(String jobOfferTitle, Long orderId){
        PaymentDto paymentDto = new PaymentDto();

        paymentDto.setSecret(secret);
        paymentDto.setAmount("50");
        paymentDto.setServiceName("Wyróżnienie oferty: " + jobOfferTitle);
        paymentDto.setWebsiteAddress(websiteAddress);
        paymentDto.setOrderId(String.valueOf(orderId));

        String stringBuilder = password + ";" +
                paymentDto.getAmount() + ";" +
                paymentDto.getServiceName() + ";" +
                paymentDto.getWebsiteAddress() + ";" +
                paymentDto.getOrderId() + ";" +
                paymentDto.getSecret();
        String hash = paymentService.hashSHA256(stringBuilder);
        paymentDto.setHash(hash);

        return paymentDto;
    }
}
