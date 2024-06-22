package com.coresaken.jobportal.auth.controller;

import com.coresaken.jobportal.auth.dto.request.SignInRequestDto;
import com.coresaken.jobportal.auth.dto.request.SignUpRequestDto;
import com.coresaken.jobportal.auth.dto.response.AuthenticationResponse;
import com.coresaken.jobportal.auth.service.AuthenticationService;
import com.coresaken.jobportal.data.dto.ResetPasswordDto;
import com.coresaken.jobportal.data.dto.StringDto;
import com.coresaken.jobportal.database.model.ResetPasswordToken;
import com.coresaken.jobportal.database.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;

    @RequestMapping("/signIn")
    public String getSignInPage(){
        return "auth/signIn";
    }

    @RequestMapping("/signUp")
    public String getSignUpPage(){
        return "auth/signUp";
    }

    @ResponseBody
    @PostMapping("/signUpPost")
    public ResponseEntity<AuthenticationResponse> signUp(@RequestBody SignUpRequestDto request, HttpServletResponse response){
        return ResponseEntity.ok(service.signUp(request, response));
    }

    @ResponseBody
    @PostMapping("/signInPost")
    public ResponseEntity<AuthenticationResponse> signIn(@RequestBody SignInRequestDto request, HttpServletResponse response){
        return ResponseEntity.ok(service.signIn(request, response));
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        service.logout(request, response);
        return "redirect:/";
    }

    @RequestMapping("/active/{code}")
    public String activeAccount(@PathVariable("code") String code){
        service.activeAccount(code);

        return "auth/signIn";
    }

    @PostMapping("/resetPasswordRequire")
    public ResponseEntity<AuthenticationResponse> resetPasswordRequire(@RequestBody StringDto stringDto) {
        AuthenticationResponse response = service.resetPassword(stringDto.getString());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/resetPassword")
    public String getResetPasswordPage(@RequestParam String token, Model model) {
        return service.getResetPasswordPage(token, model);
    }

    @PostMapping("/resetPasswordHandle")
    public ResponseEntity<AuthenticationResponse> handleResetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        AuthenticationResponse response = service.handleResetPassword(resetPasswordDto.getToken(), resetPasswordDto.getNewPassword());
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
