package com.coresaken.jobportal.service;

import com.coresaken.jobportal.data.dto.ApplicationDto;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.coresaken.jobportal.service.async.AsyncApplicationService;
import com.coresaken.jobportal.service.joboffer.JobOfferService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ApplicationService {
    final AsyncApplicationService asyncApplicationService;
    final JobOfferService jobOfferService;
    final RecaptchaService recaptchaService;

    private static final String FILE_STORAGE_LOCATION = "src/main/resources/upload";
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/png", "image/jpeg", "application/pdf");

    public Response sendApplication(ApplicationDto applicationDto){
        if(!recaptchaService.isCorrect(applicationDto.getRecaptchaToken())){
            return new Response(HttpStatus.BAD_REQUEST, "Błędna weryfikacja CAPTCHA");
        }

        JobOffer jobOffer = jobOfferService.getJobOfferByLinkUrl(applicationDto.getApplicationUrl());
        if(jobOffer == null){
            return new Response(HttpStatus.BAD_REQUEST, "Oferta już wygasła. Nie można wysyłać nowych zgłoszeń!");
        }

        if (applicationDto.getFiles() != null) {
            for (MultipartFile file : applicationDto.getFiles()) {
                try {
                    if(file==null || file.getOriginalFilename() == null){
                        continue;
                    }

                    String fileType = Files.probeContentType(Paths.get(file.getOriginalFilename()));
                    if (fileType != null && ALLOWED_FILE_TYPES.contains(fileType)) {
                        saveFile(file, applicationDto);
                    } else {
                        System.err.println("Nieprawidłowy typ pliku: " + file.getOriginalFilename());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd.");
                }
            }
        }

        asyncApplicationService.processApplication(applicationDto, jobOffer);
        return new Response(HttpStatus.OK, null);
    }

    private void saveFile(MultipartFile file, ApplicationDto applicationDto) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(FILE_STORAGE_LOCATION, fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());
        applicationDto.addFilePath(filePath.toString());
    }
}
