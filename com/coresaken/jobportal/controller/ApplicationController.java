package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.data.dto.ApplicationDto;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.service.ApplicationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@Controller
@AllArgsConstructor
public class ApplicationController {

    final ApplicationService applicationService;

    @ResponseBody
    @PostMapping("/apply")
    public Response sendApplication(@RequestPart("applicationDto") ApplicationDto applicationDto,
                                    @RequestPart(value = "files", required = false) MultipartFile[] files){
        if(applicationDto.getFirstName() == null || applicationDto.getFirstName().isEmpty()){
            return new Response(HttpStatus.BAD_REQUEST, "Musisz wprowadzić swoje imię");
        }
        if(applicationDto.getLastName() == null || applicationDto.getLastName().isEmpty()){
            return new Response(HttpStatus.BAD_REQUEST, "Musisz wprowadzić swoje nazwisko");
        }
        if(applicationDto.getUserEmail() == null || applicationDto.getUserEmail().isEmpty()){
            return new Response(HttpStatus.BAD_REQUEST, "Musisz wprowadzić swój adres email");
        }
        if (files != null) {
            long totalSize = Arrays.stream(files).mapToLong(MultipartFile::getSize).sum();
            long maxFileSize = 10 * 1024 * 1024;

            if (totalSize > maxFileSize) {
                return new Response(HttpStatus.BAD_REQUEST, "Łączny rozmiar plików musi być mniejszy niż 10MB");
            }
        }
        applicationDto.setFiles(files);

        return applicationService.sendApplication(applicationDto);
    }
}
