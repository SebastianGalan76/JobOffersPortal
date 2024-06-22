package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.data.dto.CompanyDto;
import com.coresaken.jobportal.data.dto.CompanyUserRolesDto;
import com.coresaken.jobportal.data.dto.SearchCompanyDto;
import com.coresaken.jobportal.data.response.CreateCompanyResponse;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.data.response.SearchCompanyResponse;
import com.coresaken.jobportal.database.model.*;
import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.coresaken.jobportal.database.repository.CompanyRepository;
import com.coresaken.jobportal.database.repository.CompanyUserRoleRepository;
import com.coresaken.jobportal.database.repository.LinkRepository;
import com.coresaken.jobportal.database.repository.LocationRepository;
import com.coresaken.jobportal.service.CompanyService;
import com.coresaken.jobportal.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class CompanyController {
    final CompanyService companyService;
    final UserService userService;

    final CompanyRepository companyRepository;
    final LocationRepository locationRepository;
    final LinkRepository linkRepository;
    final CompanyUserRoleRepository companyUserRoleRepository;

    final EntityManager entityManager;

    @RequestMapping("/companies")
    public String getCompaniesPage(Model model){
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("mostFollowers", findCompaniesWithMostFollowers());
        model.addAttribute("mostJobOffers", findCompaniesWithMostOffers());

        return "subPage/companies";
    }

    @RequestMapping("/c/{url}")
    public String getCompanyPage(@PathVariable("url") String url, Model model){
        Company company = companyRepository.findByLinkUrl(url).orElse(null);

        if(company==null){
            return "error-404";
        }

        User user = userService.getLoggedUser();
        model.addAttribute("user", user);
        model.addAttribute("company", company);

        List<JobOffer> publicJobOffers = new ArrayList<>();
        for(JobOffer jobOffer:company.getJobOffers()){
            if(jobOffer.getStatus() == JobOffer.Status.PUBLIC && jobOffer.getVerified()){
                publicJobOffers.add(jobOffer);
            }
        }
        model.addAttribute("jobOffers", publicJobOffers);

        if(user != null){
            if(user.getRole() == User.Role.ADMIN){
                model.addAttribute("role", CompanyUserRole.Role.ADMINISTRATOR);
            }
            else{
                for(CompanyUserRole cur:company.getCompanyUserRoles()){
                    if(cur.getUser().equals(user)){
                        if(cur.getRole() == CompanyUserRole.Role.ADMINISTRATOR || cur.getRole() == CompanyUserRole.Role.MODERATOR || cur.getRole() == CompanyUserRole.Role.RECRUITER){
                            model.addAttribute("role", cur.getRole());
                        }
                    }
                }
            }
        }

        return "subPage/company";
    }

    @ResponseBody
    @PostMapping("/company/search")
    public Page<SearchCompanyResponse.Company> getCompany(@RequestBody SearchCompanyDto searchCompanyDto) {
        Pageable pageable = PageRequest.of(searchCompanyDto.getPage() - 1, 24);

        Page<Company> foundedCompany;

        String name = searchCompanyDto.getName();
        Set<City> cities = searchCompanyDto.getCities();

        if(name != null && !name.isEmpty() && cities != null && !cities.isEmpty()){
            foundedCompany = companyRepository.searchByCitiesAndName(cities, name.toLowerCase(), pageable);
        }
        else if(name != null && !name.isEmpty()){
            foundedCompany = companyRepository.searchByName(name.toLowerCase(), pageable);
        }
        else if(cities != null && !cities.isEmpty()){
            foundedCompany = companyRepository.searchByCities(cities, pageable);
        }
        else{
            foundedCompany = companyRepository.findAll(pageable);
        }

        return foundedCompany.map(this::convertToSearchCompanyResponse);
    }

    @RequestMapping("/company/create")
    public String getCreateCompanyPage(Model model){
        User user = userService.getLoggedUser();
        model.addAttribute("user", user);
        model.addAttribute("role", CompanyUserRole.Role.ADMINISTRATOR);

        return "subPage/createCompany";
    }

    @RequestMapping("/company/manage/{name}")
    public String getManageCompanyPage(@PathVariable("name") String nameUrl, Model model){
        User user = userService.getLoggedUser();
        if(user == null){
            return "auth/signIn";
        }
        model.addAttribute("user", user);

        Company company = companyRepository.findByLinkUrl(nameUrl).orElse(null);
        if(company==null){
            return "error-404";
        }

        if(user.getRole() == User.Role.ADMIN){
            model.addAttribute("company", company);
            model.addAttribute("role", CompanyUserRole.Role.ADMINISTRATOR);
            return "subPage/createCompany";
        }
        else{
            for(CompanyUserRole cur:company.getCompanyUserRoles()){
                if(cur.getUser().equals(user)){
                    model.addAttribute("company", company);
                    model.addAttribute("role", cur.getRole());
                    return "subPage/createCompany";
                }
            }
        }

        return "auth/signIn";
    }

    @Transactional
    @ResponseBody
    @PostMapping("/company/edit/perform")
    public CreateCompanyResponse editCompany(@RequestBody CompanyDto companyDto){
        return companyService.editCompany(companyDto);
    }

    @ResponseBody
    @PostMapping("/company/create/perform")
    public CreateCompanyResponse createCompany(@RequestBody CompanyDto companyDto){
        return companyService.createCompany(companyDto);
    }

    @PostMapping("/company/preview/perform")
    public String previewCompany(@RequestBody CompanyDto companyDto, Model model) {
        model.addAttribute("user", userService.getLoggedUser());
        model.addAttribute("company", companyService.getPreviewCompany(companyDto));

        return "subPage/company";
    }

    @ResponseBody
    @PostMapping("/company/delete/{name}")
    public Response deleteCompany(@PathVariable("name") String nameUrl) {
        return companyService.deleteCompany(nameUrl);
    }
    @ResponseBody
    @PostMapping("/company/role/update/{name}")
    public Response updateRoles(@PathVariable("name") String nameUrl, @RequestBody List<CompanyUserRolesDto> companyUserRoles) {
        return companyService.updateRoles(nameUrl, companyUserRoles);
    }

    private SearchCompanyResponse.Company convertToSearchCompanyResponse(Company company) {
        StringBuilder locationStringBuilder = new StringBuilder();
        List<Location> locations = company.getLocations();

        if (!locations.isEmpty()) {
            List<City> cityList = new ArrayList<>();
            for (Location location : locations) {
                if (!cityList.contains(location.getCity())) {
                    cityList.add(location.getCity());
                }
            }

            if (cityList.size() == 1) {
                locationStringBuilder.append(cityList.get(0).getName());
            } else if (cityList.size() == 2) {
                locationStringBuilder.append(cityList.get(0).getName())
                        .append(", ")
                        .append(cityList.get(1).getName());
            } else {
                locationStringBuilder.append(cityList.get(0).getName())
                        .append(", ")
                        .append(cityList.get(1).getName())
                        .append(", +")
                        .append(cityList.size() - 2)
                        .append(" lokalizacji");
            }
        }

        return new SearchCompanyResponse.Company(
                company.getId(),
                company.getName(),
                company.getLinkUrl(),
                company.getLogoUrl(),
                locationStringBuilder.toString()
        );
    }

    private List<Company> findCompaniesWithMostFollowers() {
        return entityManager.createQuery(
                        "SELECT c FROM Company c ORDER BY SIZE(c.followedUsers) DESC", Company.class)
                .setMaxResults(5)
                .getResultList();
    }

    private List<Company> findCompaniesWithMostOffers() {
        return entityManager.createQuery(
                        "SELECT c FROM Company c ORDER BY c.totalOffers DESC", Company.class)
                .setMaxResults(5)
                .getResultList();
    }
}
