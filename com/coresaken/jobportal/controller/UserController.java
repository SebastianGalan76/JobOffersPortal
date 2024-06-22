package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.auth.dto.response.AuthenticationResponse;
import com.coresaken.jobportal.data.dto.ChangePasswordDto;
import com.coresaken.jobportal.data.dto.ChangePersonalDataDto;
import com.coresaken.jobportal.data.dto.SearchDto;
import com.coresaken.jobportal.data.response.CompanyResponse;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.database.model.Company;
import com.coresaken.jobportal.database.model.CompanyUserRole;
import com.coresaken.jobportal.database.model.User;
import com.coresaken.jobportal.database.model.UserDetail;
import com.coresaken.jobportal.database.repository.UserRepository;
import com.coresaken.jobportal.database.repository.joboffer.JobOfferRepository;
import com.coresaken.jobportal.service.UserService;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@AllArgsConstructor
public class UserController {
    final UserRepository userRepository;
    final UserService userService;
    final JobOfferRepository jobOfferRepository;
    final PasswordEncoder passwordEncoder;

    @ResponseBody
    @PostMapping("/user")
    public ResponseEntity<User> getUserByEmail(@RequestBody SearchDto searchDto) {
        User user = userService.getUserByEmail(searchDto.getValue());

        if(user == null){
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(user);
    }

    @RequestMapping("/user/panel")
    public String getUserPanel(Model model){
        User user = userService.getLoggedUser();
        if(user == null){
            return "auth/signIn";
        }

        Set<CompanyResponse> companies = new HashSet<>();
        for(CompanyUserRole cur:user.getCompanyUserRoles()){
            Company company = cur.getCompany();
            companies.add(new CompanyResponse(company.getName(), company.getLogoUrl(), company.getLinkUrl()));
        }
        Set<CompanyResponse> followedCompanies = new HashSet<>();
        for(Company company:user.getFollowedCompany()){
            followedCompanies.add(new CompanyResponse(company.getName(), company.getLogoUrl(), company.getLinkUrl()));
        }

        model.addAttribute("followedCompanies", followedCompanies);
        model.addAttribute("companies", companies);
        model.addAttribute("jobOffers", jobOfferRepository.findAllJobOffersForUser(user));
        model.addAttribute("user", user);

        return "subPage/userPanel";
    }

    @ResponseBody
    @PostMapping("/user/changePassword")
    public Response changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        User user = userService.getLoggedUser();
        if(user == null){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła, zaloguj się ponownie!");
        }

        if(changePasswordDto.getNewPassword().length()<4){
            return new Response(HttpStatus.BAD_REQUEST, "Nowe hasło jest zbyt krótkie!");
        }

        if(!passwordEncoder.matches(changePasswordDto.getPassword(), user.getPassword())){
            return new Response(HttpStatus.BAD_REQUEST, "Podane hasło nie jest poprawne!");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
        return new Response(HttpStatus.OK, null);
    }

    @ResponseBody
    @PostMapping("/user/changePersonalData")
    public Response changePersonalData(@RequestBody ChangePersonalDataDto changePersonalDataDto){
        User user = userService.getLoggedUser();
        if(user == null){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła, zaloguj się ponownie!");
        }

        UserDetail userDetail = user.getUserDetail();
        userDetail.setFirstName(changePersonalDataDto.getFirstName());
        userDetail.setLastName(changePersonalDataDto.getLastName());
        user.setUserDetail(userDetail);
        userRepository.save(user);
        return new Response(HttpStatus.OK, null);
    }
}
