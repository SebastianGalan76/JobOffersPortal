package com.coresaken.jobportal.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDto {
    String firstName;
    String lastName;
    String userEmail;
    String message;

    String applicationUrl;

    String recaptchaToken;
    MultipartFile[] files;

    List<String> filePaths;

    public void addFilePath(String filePath) {
        if (filePaths == null) {
            filePaths = new ArrayList<>();
        }
        filePaths.add(filePath);
    }
}
