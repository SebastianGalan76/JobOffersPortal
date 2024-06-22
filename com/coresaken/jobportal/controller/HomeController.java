package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
public class HomeController {
    final UserService userService;

    @RequestMapping("/")
    public String getHomePage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        return "home";
    }

    @RequestMapping("/help")
    public String getHelpPage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        return "subPage/help";
    }

    @RequestMapping("/prices")
    public String getPricesPage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        return "subPage/prices";
    }

    @RequestMapping("/regulamin")
    public String getRulesPage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        return "subPage/rules";
    }

    @RequestMapping("/privacy-policy")
    public String getPrivacyPolicyPage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        return "subPage/privacyPolicy";
    }

    @RequestMapping("/contact")
    public String getContactPage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        return "subPage/contact";
    }

    @RequestMapping("/too-many-redirects")
    public String getTooManyRedirectsPage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        return "error-310";
    }
}
