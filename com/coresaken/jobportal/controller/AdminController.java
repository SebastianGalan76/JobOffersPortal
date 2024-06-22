package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.database.model.User;
import com.coresaken.jobportal.database.repository.joboffer.JobOfferRepository;
import com.coresaken.jobportal.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
public class AdminController {
    final UserService userService;
    final JobOfferRepository jobOfferRepository;

    @RequestMapping("/admin")
    public String getAdminPanel(Model model){
        User user = userService.getLoggedUser();
        if(user == null || user.getRole() != User.Role.ADMIN){
            return "/auth/signIn";
        }

        model.addAttribute("user", user);
        model.addAttribute("unverifiedOffers", jobOfferRepository.findAllUnverifiedOffers());

        return "subPage/adminPanel";
    }
}
