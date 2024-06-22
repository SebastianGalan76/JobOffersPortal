package com.coresaken.jobportal.service;

import com.coresaken.jobportal.database.model.Company;
import com.coresaken.jobportal.database.model.User;
import com.coresaken.jobportal.database.repository.CompanyRepository;
import com.coresaken.jobportal.database.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class FollowService {
    final UserService userService;

    final CompanyRepository companyRepository;
    final UserRepository userRepository;

    public ResponseEntity<String> follow(Long companyId) {
        User loggedInUser = userService.getLoggedUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if (companyOptional.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        Company company = companyOptional.get();
        if(!loggedInUser.getFollowedCompany().contains(company)){
            loggedInUser.getFollowedCompany().add(company);
            company.getFollowedUsers().add(loggedInUser);

            userRepository.save(loggedInUser);
            companyRepository.save(company);
            return ResponseEntity.ok("User now follows company.");
        } else {
            return ResponseEntity.badRequest().body("User already follows this company.");
        }
    }

    public ResponseEntity<String> unfollow(Long companyId) {
        User loggedInUser = userService.getLoggedUser();
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }

        Optional<Company> companyOptional = companyRepository.findById(companyId);
        if (companyOptional.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        Company company = companyOptional.get();

        if(loggedInUser.getFollowedCompany().contains(company)){
            loggedInUser.getFollowedCompany().remove(company);
            company.getFollowedUsers().remove(loggedInUser);

            userRepository.save(loggedInUser);
            companyRepository.save(company);

            userRepository.unfollowCompanyById(loggedInUser.getId(), company.getId());
            return ResponseEntity.ok("The user no longer follows this company.");
        } else {
            return ResponseEntity.badRequest().body("User doesn't follow this company.");
        }
    }
}
