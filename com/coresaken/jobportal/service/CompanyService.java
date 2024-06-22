package com.coresaken.jobportal.service;

import com.coresaken.jobportal.data.dto.CompanyDto;
import com.coresaken.jobportal.data.dto.CompanyUserRolesDto;
import com.coresaken.jobportal.data.response.CreateCompanyResponse;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.database.model.*;
import com.coresaken.jobportal.database.model.key.CompanyUserRoleKey;
import com.coresaken.jobportal.database.repository.CompanyRepository;
import com.coresaken.jobportal.database.repository.CompanyUserRoleRepository;
import com.coresaken.jobportal.database.repository.LinkRepository;
import com.coresaken.jobportal.database.repository.LocationRepository;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class CompanyService {
    final UserService userService;

    final LinkRepository linkRepository;
    final CompanyRepository companyRepository;
    final LocationRepository locationRepository;
    final CompanyUserRoleRepository companyUserRoleRepository;

    @Transactional
    public CreateCompanyResponse createCompany(CompanyDto companyDto) {
        User user = userService.getLoggedUser();
        if (user == null) {
            return new CreateCompanyResponse(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Zaloguj się ponownie na stronie.", null);
        }

        if (companyDto.getName().isEmpty()) {
            return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "Nazwa firmy nie może być pusta.", null);
        }
        if (companyDto.getLinkUrl().isEmpty()) {
            return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "URL firmy nie może być pusty.", null);
        }
        if (companyRepository.existsByName(companyDto.getName())) {
            return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "Wprowadzona nazwa jest już zajęta.", null);
        }
        if (companyRepository.existsByLinkUrl(companyDto.getLinkUrl())) {
            return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "Wprowadzony URL firmy jest już zajęty.", null);
        }

        if (companyDto.getLogoUrl().length() <= 5) {
            companyDto.setLogoUrl(null);
        }

        if (companyDto.getBackgroundUrl().length() <= 5) {
            companyDto.setBackgroundUrl(null);
        }

        //Saves new locations
        if (companyDto.getLocations() != null) {
            List<Location> locations = new ArrayList<>();

            for (Location location : companyDto.getLocations()) {
                locations.add(locationRepository.save(location));
            }

            companyDto.setLocations(locations);
        }
        //Saves new links
        if (companyDto.getLinks() != null) {
            List<Link> links = new ArrayList<>();

            for (Link link : companyDto.getLinks()) {
                links.add(linkRepository.save(link));
            }

            companyDto.setLinks(links);
        }

        Company newCompany = new Company();
        try {
            newCompany.setName(companyDto.getName());
            newCompany.setLinkUrl(sanitizeUrl(companyDto.getLinkUrl()));
            newCompany.setDescription(companyDto.getDescription());
            newCompany.setLogoUrl(companyDto.getLogoUrl());
            newCompany.setBackgroundUrl(companyDto.getBackgroundUrl());
            newCompany.setLocations(companyDto.getLocations());
            newCompany.setLinks(companyDto.getLinks());
            newCompany.setTotalOffers(0);

            newCompany = companyRepository.save(newCompany);

            //Add administrator role to the creator of the companyDto
            newCompany.getCompanyUserRoles().add(companyUserRoleRepository.save(createCompanyUserRole(newCompany, user, CompanyUserRole.Role.ADMINISTRATOR)));
        } catch (DataIntegrityViolationException e) {
            return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "Wprowadzona nazwa firmy lub link url jest już zajęty! Jeśli ktoś zajął nazwę Twojej firmy, to zgłoś się do nas.", null);
        }

        return new CreateCompanyResponse(HttpStatus.OK, null, "/c/" + companyDto.getLinkUrl());
    }

    @Transactional
    public CreateCompanyResponse editCompany(CompanyDto companyDto) {
        User user = userService.getLoggedUser();
        if (user == null) {
            return new CreateCompanyResponse(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Zaloguj się ponownie na stronie.", null);
        }
        Company savedCompany = companyRepository.getReferenceById(companyDto.getId());

        if (user.getRole() != User.Role.ADMIN) {
            for (CompanyUserRole cur : savedCompany.getCompanyUserRoles()) {
                if (Objects.equals(cur.getUser().getId(), user.getId())) {
                    if (!(cur.getRole() == CompanyUserRole.Role.ADMINISTRATOR || cur.getRole() == CompanyUserRole.Role.MODERATOR)) {
                        return new CreateCompanyResponse(HttpStatus.UNAUTHORIZED, "Nie masz uprawnień, aby edytować tę firmę.", null);
                    }
                }
            }
        }

        if (!savedCompany.getName().equals(companyDto.getName())) {
            if (companyRepository.existsByName(companyDto.getName())) {
                return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "Wprowadzona nazwa firmy jest już zajęta.", null);
            }
        }
        if (!savedCompany.getLinkUrl().equals(companyDto.getLinkUrl())) {
            if (companyRepository.existsByLinkUrl(companyDto.getLinkUrl())) {
                return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "Wprowadzony URL firmy jest już zajęty.", null);
            }
        }

        //Finds locations that have been deleted
        List<Location> locationsToRemove = new ArrayList<>(savedCompany.getLocations());
        locationsToRemove.removeAll(companyDto.getLocations());

        //Saves new locations
        List<Location> locations = new ArrayList<>();
        for (Location location : companyDto.getLocations()) {
            locations.add(locationRepository.save(location));
        }
        companyDto.setLocations(locations);

        //Finds links that have been deleted
        List<Link> linksToRemove = new ArrayList<>(savedCompany.getLinks());
        linksToRemove.removeAll(companyDto.getLinks());

        //Saves new links
        List<Link> links = new ArrayList<>();
        for (Link link : companyDto.getLinks()) {
            links.add(linkRepository.save(link));
        }
        companyDto.setLinks(links);

        if (companyDto.getLogoUrl().length() <= 5) {
            companyDto.setLogoUrl(null);
        }
        if (companyDto.getBackgroundUrl().length() <= 5) {
            companyDto.setBackgroundUrl(null);
        }

        try {
            savedCompany.setName(companyDto.getName());
            savedCompany.setLinkUrl(sanitizeUrl(companyDto.getLinkUrl()));
            savedCompany.setDescription(companyDto.getDescription());
            savedCompany.setLogoUrl(companyDto.getLogoUrl());
            savedCompany.setBackgroundUrl(companyDto.getBackgroundUrl());
            savedCompany.setLocations(companyDto.getLocations());
            savedCompany.setLinks(companyDto.getLinks());

            companyRepository.save(savedCompany);

            locationRepository.deleteAll(locationsToRemove);
            linkRepository.deleteAll(linksToRemove);
        } catch (DataIntegrityViolationException e) {
            return new CreateCompanyResponse(HttpStatus.BAD_REQUEST, "Wprowadzona nazwa lub nazwa url jest już zajęta.", null);
        }

        return new CreateCompanyResponse(HttpStatus.OK, null, "/c/" + savedCompany.getLinkUrl());
    }

    @Transactional
    public Response deleteCompany(String nameUrl) {
        Company company = companyRepository.findByLinkUrl(nameUrl).orElse(null);

        if(company==null){
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd podczas usuwania profilu firmy. Jeśli błąd się powtórzy, proszę skontaktować się z Administracją strony.");
        }
        User user = userService.getLoggedUser();
        if(user == null){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Proszę się ponownie zalogować.");
        }
        if(user.getRole() != User.Role.ADMIN){
            for(CompanyUserRole cur:company.getCompanyUserRoles()){
                if(cur.getUser().equals(user)){
                    if(cur.getRole() != CompanyUserRole.Role.ADMINISTRATOR){
                        return new Response(HttpStatus.BAD_REQUEST, "Nie posiadasz wymaganych uprawnień, aby usunąć profil firmy.");
                    }
                }
            }
        }
        companyRepository.delete(company);
        return new Response(HttpStatus.OK, null);
    }

    public Company getPreviewCompany(CompanyDto companyDto){
        if (companyDto.getLogoUrl().length() <= 5) {
            companyDto.setLogoUrl(null);
        }

        if (companyDto.getBackgroundUrl().length() <= 5) {
            companyDto.setBackgroundUrl(null);
        }

        Company newCompany = new Company();
        newCompany.setId(0L);
        newCompany.setName(companyDto.getName());
        newCompany.setLinkUrl(sanitizeUrl(companyDto.getLinkUrl()));
        newCompany.setDescription(companyDto.getDescription());
        newCompany.setLogoUrl(companyDto.getLogoUrl());
        newCompany.setBackgroundUrl(companyDto.getBackgroundUrl());
        newCompany.setLocations(companyDto.getLocations());
        newCompany.setLinks(companyDto.getLinks());

        return newCompany;
    }

    @Transactional
    public Response updateRoles(String linkUrl, List<CompanyUserRolesDto> companyUserRolesDto){
        Company company = companyRepository.findByLinkUrl(linkUrl).orElse(null);

        if(company == null){
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd. Spróbuj ponownie później lub skontaktuj się z Administratorem strony.");
        }

        User user = userService.getLoggedUser();
        if(user==null){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Zaloguj się ponownie na stronie.");
        }

        if(user.getRole()!= User.Role.ADMIN){
            for(CompanyUserRole cur:company.getCompanyUserRoles()){
                if(cur.getUser().equals(user)){
                    if(cur.getRole() != CompanyUserRole.Role.ADMINISTRATOR){
                        return new Response(HttpStatus.UNAUTHORIZED, "Tylko administrator profilu firmy może zmieniać uprawnienia!");
                    }
                }
            }
        }

        List<Long> roleIdToRemove = new ArrayList<>();
        for(CompanyUserRole cur:company.getCompanyUserRoles()){
            boolean found = false;
            for(CompanyUserRolesDto curDto:companyUserRolesDto){
                if(Objects.equals(cur.getUser().getEmail(), curDto.getUserEmail())){
                    found = true;
                    break;
                }
            }

            if(!found){
                roleIdToRemove.add(cur.getUser().getId());
            }
        }
        for(Long id:roleIdToRemove){
            companyUserRoleRepository.deleteRoleForCompany(company.getId(), id);
        }

        if (companyUserRolesDto != null) {
            Set<CompanyUserRole> companyUserRoles = new HashSet<>();

            for (CompanyUserRolesDto companyUserRoleDto : companyUserRolesDto) {
                User userRole = userService.getUserByEmail(companyUserRoleDto.getUserEmail());
                if (userRole == null) {
                    continue;
                }

                CompanyUserRole cur = companyUserRoleRepository.save(createCompanyUserRole(company, userRole, companyUserRoleDto.getRole()));
                companyUserRoles.add(cur);
            }

            company.setCompanyUserRoles(companyUserRoles);
        }
        companyRepository.save(company);

        return new Response(HttpStatus.OK, null);
    }

    @Nullable
    public Company getCompanyById(Long id){
        return companyRepository.findById(id).orElse(null);
    }

    private CompanyUserRole createCompanyUserRole(Company company, User user, CompanyUserRole.Role role) {
        CompanyUserRoleKey companyUserRoleKey = new CompanyUserRoleKey(company.getId(), user.getId());

        CompanyUserRole companyUserRole = new CompanyUserRole();
        companyUserRole.setId(companyUserRoleKey);
        companyUserRole.setCompany(company);
        companyUserRole.setUser(user);
        companyUserRole.setRole(role);

        return companyUserRole;
    }

    public static String sanitizeUrl(String inputText) {
        String sanitizedText = inputText.replaceAll("\\s+", "_");
        sanitizedText = sanitizedText.replaceAll("[^a-zA-Z0-9_.]", "");
        return sanitizedText.toLowerCase();
    }

}
