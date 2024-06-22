package com.coresaken.jobportal.service.async;

import com.coresaken.jobportal.data.dto.ApplicationDto;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.coresaken.jobportal.service.EmailSenderService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsyncApplicationService {

    final EmailSenderService emailSenderService;

    @Async
    public void processApplication(ApplicationDto applicationDto, JobOffer jobOffer) {
        try {
            emailSenderService.sendApplication(applicationDto, jobOffer);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
