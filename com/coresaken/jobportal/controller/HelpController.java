package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.data.dto.HelpMessageDto;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.service.EmailSenderService;
import com.coresaken.jobportal.service.RecaptchaService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
public class HelpController {
    final EmailSenderService emailSenderService;
    final RecaptchaService recaptchaService;

    @ResponseBody
    @PostMapping("/help/send")
    public Response sendMessage(@RequestBody HelpMessageDto helpMessageDto){
        if(!recaptchaService.isCorrect(helpMessageDto.getRecaptchaToken())){
            return new Response(HttpStatus.BAD_REQUEST, "Błędna weryfikacja CAPTCHA");
        }

        try{
            emailSenderService.sendHelpMessage(helpMessageDto);
        } catch (MessagingException e) {
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił błąd przesyłu wiadomości. Spróbuj ponownie za jakiś czas lub użyj innej formy kontaktu.");
        }

        return new Response(HttpStatus.OK, null);
    }
}
