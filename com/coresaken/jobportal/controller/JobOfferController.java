package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.data.dto.ManageJobOfferDto;
import com.coresaken.jobportal.data.dto.SearchJobOfferDto;
import com.coresaken.jobportal.data.response.CreateJobOfferResponse;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.database.model.Company;
import com.coresaken.jobportal.database.model.CompanyUserRole;
import com.coresaken.jobportal.database.model.User;
import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.coresaken.jobportal.database.repository.joboffer.JobOfferRepository;
import com.coresaken.jobportal.service.ApplicationService;
import com.coresaken.jobportal.service.joboffer.JobOfferService;
import com.coresaken.jobportal.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Controller
@Data
@AllArgsConstructor
public class JobOfferController{
    final JobOfferService jobOfferService;

    final ApplicationService applicationService;
    final UserService userService;

    final JobOfferRepository repository;

    @ResponseBody
    @PostMapping("/jobOffers")
    public Page<JobOffer> getJobOffers(@RequestBody SearchJobOfferDto searchJobOfferDto){
        return jobOfferService.getJobOffers(searchJobOfferDto);
    }

    @RequestMapping("/offer/{url}")
    public String getOfferPage(@PathVariable("url") String url, Model model){
        JobOffer jobOffer = jobOfferService.getJobOfferByLinkUrl(url);
        if(jobOffer == null){
            return "error-404";
        }

        StringBuilder title = new StringBuilder();
        title.append(jobOffer.getTitle());
        if(jobOffer.getCompany() != null){
            title.append(" dla ").append(jobOffer.getCompany().getName());
        }
        else if(jobOffer.getSimpleCompany() != null && jobOffer.getSimpleCompany().getName() != null){
            title.append(" dla ").append(jobOffer.getSimpleCompany().getName());
        }
        model.addAttribute("title", title);

        User user = userService.getLoggedUser();
        if(user!=null){
            model.addAttribute("userFirstName", user.getUserDetail().getFirstName());
            model.addAttribute("userLastName", user.getUserDetail().getLastName());
            model.addAttribute("userEmail", user.getEmail());

            if(user.getRole() == User.Role.ADMIN){
                model.addAttribute("role", CompanyUserRole.Role.ADMINISTRATOR);
            }
            else{
                Company company = jobOffer.getCompany();
                if(company != null){
                    for(CompanyUserRole cur:company.getCompanyUserRoles()){
                        if(cur.getUser().equals(user)){
                            model.addAttribute("role", cur.getRole());
                        }
                    }
                }
                else{
                    if(jobOffer.getUser().equals(user)){
                        model.addAttribute("role", CompanyUserRole.Role.ADMINISTRATOR);
                    }
                }
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("jobOffer", jobOffer);

        String redirect = jobOffer.getAction().getRedirect();
        if(redirect!=null && redirect.length()>5){
            model.addAttribute("action",redirect);
        }
        else{
            model.addAttribute("action", "email");
        }

        return "subPage/jobOffer";
    }

    @RequestMapping("/jobOffer/create")
    public String getCreateJobOfferPage(Model model){
        User user = userService.getLoggedUser();

        model.addAttribute("manage", false);
        model.addAttribute("user", user);
        if(user==null){
            model.addAttribute("companyUserRoles", null);
        }
        else{
            model.addAttribute("companyUserRoles", user.getCompanyUserRoles());
        }

        return "subPage/createJobOffer";
    }
    @RequestMapping("/jobOffer/create/{link}")
    public String getCreateJobOfferPage(@PathVariable("link") String linkUrl, Model model){
        User user = userService.getLoggedUser();

        JobOffer jobOffer = jobOfferService.getJobOfferByLinkUrl(linkUrl);
        jobOffer.setPromotionTier(0);

        model.addAttribute("jobOffer", jobOffer);
        model.addAttribute("manage", false);
        model.addAttribute("user", user);
        if(user==null){
            model.addAttribute("companyUserRoles", null);
        }
        else{
            model.addAttribute("companyUserRoles", user.getCompanyUserRoles());
        }

        return "subPage/createJobOffer";
    }

    @RequestMapping("/jobOffer/manage/{link}")
    public String getManageJobOfferPage(@PathVariable("link") String linkUrl, Model model){
        User user = userService.getLoggedUser();
        if(user == null){
            return "auth/signIn";
        }
        model.addAttribute("user", user);

        JobOffer jobOffer = repository.findByLinkUrl(linkUrl).orElse(null);
        if(jobOffer==null){
            return "error-404";
        }

        model.addAttribute("manage", true);
        if(user.getRole() == User.Role.ADMIN){
            model.addAttribute("jobOffer", jobOffer);

            Set<CompanyUserRole> companyUserRoleSet = new HashSet<>();
            companyUserRoleSet.addAll(user.getCompanyUserRoles());
            companyUserRoleSet.addAll(jobOffer.getUser().getCompanyUserRoles());

            model.addAttribute("companyUserRoles", companyUserRoleSet);
            return "subPage/createJobOffer";
        }
        else {
            if(jobOffer.getUser().equals(user) && jobOffer.getCompany()==null){
                model.addAttribute("jobOffer", jobOffer);
                model.addAttribute("companyUserRoles", user.getCompanyUserRoles());
                return "subPage/createJobOffer";
            }
            if(jobOffer.getCompany() != null){
                for(CompanyUserRole cur:jobOffer.getCompany().getCompanyUserRoles()){
                    if(cur.getUser().equals(user)){
                        model.addAttribute("jobOffer", jobOffer);
                        model.addAttribute("companyUserRoles", user.getCompanyUserRoles());
                        return "subPage/createJobOffer";
                    }
                }
            }
        }

        return "auth/signIn";
    }
    @ResponseBody
    @PostMapping("/jobOffer/delete/{name}")
    public Response deleteJobOffer(@PathVariable("name") String nameUrl) {
        return jobOfferService.deleteJobOffer(nameUrl);
    }

    @ResponseBody
    @PostMapping("/jobOffer/create/perform")
    public Response createJobOffer(@RequestBody ManageJobOfferDto manageJobOfferDto){
        return  jobOfferService.createJobOffer(manageJobOfferDto);
    }

    @Transactional
    @ResponseBody
    @PostMapping("/jobOffer/edit/perform")
    public CreateJobOfferResponse editJobOffer(@RequestBody ManageJobOfferDto manageJobOfferDto){
        return jobOfferService.editJobOffer(manageJobOfferDto);
    }

    @PostMapping("/jobOffer/preview/perform")
    public String previewCompany(@RequestBody ManageJobOfferDto manageJobOfferDto, Model model) {
        User user = userService.getLoggedUser();
        JobOffer previewOffer = jobOfferService.getPreviewJobOffer(manageJobOfferDto);

        model.addAttribute("user", user);
        model.addAttribute("jobOffer",previewOffer);

        if(previewOffer.getAction().getRedirect()!=null && !previewOffer.getAction().getRedirect().isEmpty()){
            model.addAttribute("action", previewOffer.getAction());
        }
        else{
            model.addAttribute("action", "email");
        }

        return "subPage/jobOffer";
    }

    @ResponseBody
    @PostMapping("/jobOffer/visibility")
    public Response changeVisibility(@RequestBody Map<String, Object> payload) {
        Long jobOfferId = Long.valueOf(payload.get("jobOfferId").toString());
        String visibility = payload.get("visibility").toString();

        try{
            JobOffer.Status status = JobOffer.Status.valueOf(visibility);
            return jobOfferService.changeVisibility(jobOfferId, status);
        }
        catch (IllegalArgumentException e){
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd (#3211). Spróbuj ponownie lub skontaktuj się z Administracją!");
        }
    }
    @ResponseBody
    @PostMapping("/jobOffer/accept")
    public Response acceptOffer(@RequestBody Map<String, Object> payload) {
        Long jobOfferId = Long.valueOf(payload.get("jobOfferId").toString());
        return jobOfferService.acceptOffer(jobOfferId);
    }

    @ResponseBody
    @PostMapping("/jobOffer/reject")
    public Response rejectOffer(@RequestBody Map<String, Object> payload) {
        Long jobOfferId = Long.valueOf(payload.get("jobOfferId").toString());
        String reason = payload.get("rejectReason").toString();
        if(reason == null || reason.isEmpty()){
            reason = "Oferta niezgodna z naszym regulaminem";
        }
        return jobOfferService.rejectOffer(jobOfferId, reason);
    }
}
