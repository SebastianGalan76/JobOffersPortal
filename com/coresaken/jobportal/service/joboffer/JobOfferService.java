package com.coresaken.jobportal.service.joboffer;

import com.coresaken.jobportal.data.SimpleCompany;
import com.coresaken.jobportal.data.dto.ManageJobOfferDto;
import com.coresaken.jobportal.data.dto.SearchJobOfferDto;
import com.coresaken.jobportal.data.payment.JobOfferPromotion;
import com.coresaken.jobportal.data.response.CreateJobOfferResponse;
import com.coresaken.jobportal.data.response.Response;
import com.coresaken.jobportal.database.model.*;
import com.coresaken.jobportal.database.model.joboffer.*;
import com.coresaken.jobportal.database.repository.CompanyRepository;
import com.coresaken.jobportal.database.repository.LocationRepository;
import com.coresaken.jobportal.database.repository.joboffer.JobOfferRepository;
import com.coresaken.jobportal.service.CompanyService;
import com.coresaken.jobportal.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class JobOfferService {
    final JobOfferRepository jobOfferRepository;
    final UserService userService;
    final CompanyService companyService;
    final LocationRepository locationRepository;
    final CompanyRepository companyRepository;

    final CategoryService categoryService;
    final ExperienceLevelService experienceLevelService;
    final EmploymentTypeService employmentTypeService;
    final WorkTypeService workTypeService;

    public Page<JobOffer> getJobOffers(SearchJobOfferDto searchJobOfferDto){
        if(searchJobOfferDto.getSelectedCategories() == null || searchJobOfferDto.getSelectedCategories().isEmpty()){
            searchJobOfferDto.setSelectedCategories(null);
        }

        if(searchJobOfferDto.getSelectedCities() == null || searchJobOfferDto.getSelectedCities().isEmpty()){
            searchJobOfferDto.setSelectedCities(null);
        }

        if(searchJobOfferDto.getSelectedWorkTypes() == null || searchJobOfferDto.getSelectedWorkTypes().isEmpty()){
            searchJobOfferDto.setSelectedWorkTypes(null);
        }

        if(searchJobOfferDto.getSelectedEmploymentTypes() == null || searchJobOfferDto.getSelectedEmploymentTypes().isEmpty()){
            searchJobOfferDto.setSelectedEmploymentTypes(null);
        }

        if(searchJobOfferDto.getSelectedExperienceLevels() == null || searchJobOfferDto.getSelectedExperienceLevels().isEmpty()){
            searchJobOfferDto.setSelectedExperienceLevels(null);
        }


        Pageable pageable = PageRequest.of(searchJobOfferDto.getPage() - 1, 20, Sort.by("refreshedAt").descending().and(Sort.by("promotionTier").descending().and(Sort.by("createdAt").descending())));

        return  jobOfferRepository.getJobOffers(
                searchJobOfferDto.getSelectedCategories(),
                searchJobOfferDto.getSelectedCities(),
                searchJobOfferDto.getSelectedWorkTypes(),
                searchJobOfferDto.getSelectedEmploymentTypes(),
                searchJobOfferDto.getSelectedExperienceLevels(),
                JobOffer.Status.PUBLIC,
                pageable);

    }

    public JobOffer getJobOfferByLinkUrl(String url){
        return jobOfferRepository.findByLinkUrl(url).orElse(null);
    }

    @Transactional
    public CreateJobOfferResponse createJobOffer(ManageJobOfferDto manageJobOfferDto) {
        JobOffer jobOffer = new JobOffer();

        User user = userService.getLoggedUser();
        if(user==null){
            return new CreateJobOfferResponse(HttpStatus.UNAUTHORIZED, "", null);
        }

        Company company = companyService.getCompanyById(manageJobOfferDto.getCompanyId());
        if(company != null){
            jobOffer.setCompany(company);
        }
        else{
            SimpleCompany simpleCompany = manageJobOfferDto.getSimpleCompany();
            if(simpleCompany==null){
                return new CreateJobOfferResponse(HttpStatus.BAD_REQUEST, "", null);
            }

            jobOffer.setSimpleCompany(simpleCompany);
        }

        jobOffer.setTitle(manageJobOfferDto.getTitle());
        jobOffer.setDescription(manageJobOfferDto.getDescription());
        jobOffer.setAction(manageJobOfferDto.getAction());
        jobOffer.setBackgroundUrl(manageJobOfferDto.getBackgroundUrl());
        jobOffer.setSalary(manageJobOfferDto.getSalary());
        jobOffer.setTags(manageJobOfferDto.getTags());
        jobOffer.setVerified(false);
        jobOffer.setUser(user);

        Category category = categoryService.getCategoryById(manageJobOfferDto.getCategoryId());
        if(category==null){
            return new CreateJobOfferResponse(HttpStatus.BAD_REQUEST, "", null);
        }
        jobOffer.setCategory(category);

        try{
            JobOffer.Status status = JobOffer.Status.valueOf(manageJobOfferDto.getVisibility());
            jobOffer.setStatus(status);
        }catch (IllegalArgumentException e){
            jobOffer.setStatus(JobOffer.Status.PRIVATE);
        }


        List<Location> locations = new ArrayList<>();
        for (Location location : manageJobOfferDto.getLocations()) {
            locations.add(locationRepository.save(location));
        }
        jobOffer.setLocations(locations);

        for(Long id: manageJobOfferDto.getSelectedExperienceLevels()){
            ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelById(id);

            if(experienceLevel!=null){
                jobOffer.getExperienceLevels().add(experienceLevel);
            }
        }
        for(String name: manageJobOfferDto.getCustomExperienceLevels()){
            ExperienceLevel experienceLevel = experienceLevelService.addCustomExperienceLevel(name);

            jobOffer.getExperienceLevels().add(experienceLevel);
        }

        for(Long id: manageJobOfferDto.getSelectedEmploymentTypes()){
            EmploymentType employmentType = employmentTypeService.getEmploymentTypeById(id);

            if(employmentType!=null){
                jobOffer.getEmploymentTypes().add(employmentType);
            }
        }
        for(String name: manageJobOfferDto.getCustomEmploymentTypes()){
            EmploymentType employmentType = employmentTypeService.addCustomEmploymentType(name);

            jobOffer.getEmploymentTypes().add(employmentType);
        }

        for(Long id: manageJobOfferDto.getSelectedWorkTypes()){
            WorkType workType = workTypeService.getWorkTypeById(id);

            if(workType!=null){
                jobOffer.getWorkTypes().add(workType);
            }
        }
        for(String name: manageJobOfferDto.getCustomWorkTypes()){
            WorkType workType = workTypeService.addCustomWorkType(name);

            jobOffer.getWorkTypes().add(workType);
        }

        jobOffer = jobOfferRepository.save(jobOffer);
        return new CreateJobOfferResponse(HttpStatus.OK, null, "/offer/"+jobOffer.getLinkUrl());
    }

    @Transactional
    public CreateJobOfferResponse editJobOffer(ManageJobOfferDto manageJobOfferDto){
        User user = userService.getLoggedUser();
        if (user == null) {
            return new CreateJobOfferResponse(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Zaloguj się ponownie na stronie.", null);
        }

        JobOffer savedJobOffer = jobOfferRepository.getReferenceById(manageJobOfferDto.getId());

        if (user.getRole() != User.Role.ADMIN) {
            Company company = savedJobOffer.getCompany();
            if(company!=null){
                for (CompanyUserRole cur : company.getCompanyUserRoles()) {
                    if (Objects.equals(cur.getUser().getId(), user.getId())) {
                        if (!(cur.getRole() == CompanyUserRole.Role.ADMINISTRATOR || cur.getRole() == CompanyUserRole.Role.MODERATOR || cur.getRole() == CompanyUserRole.Role.RECRUITER)) {
                            return new CreateJobOfferResponse(HttpStatus.UNAUTHORIZED, "Nie masz uprawnień, aby edytować te ogłoszenie.", null);
                        }
                    }
                }
            }
            else{
                if(!user.equals(savedJobOffer.getUser())){
                    return new CreateJobOfferResponse(HttpStatus.UNAUTHORIZED, "Nie masz uprawnień, aby edytować te ogłoszenie.", null);
                }
            }
        }

        Company company = companyService.getCompanyById(manageJobOfferDto.getCompanyId());
        if(company != null){
            savedJobOffer.setCompany(company);
            savedJobOffer.setSimpleCompany(null);
        }
        else{
            SimpleCompany simpleCompany = manageJobOfferDto.getSimpleCompany();
            if(simpleCompany==null){
                return new CreateJobOfferResponse(HttpStatus.BAD_REQUEST, "", null);
            }

            savedJobOffer.setSimpleCompany(simpleCompany);
            savedJobOffer.setCompany(null);
        }

        savedJobOffer.setTitle(manageJobOfferDto.getTitle());
        savedJobOffer.setDescription(manageJobOfferDto.getDescription());
        savedJobOffer.setAction(manageJobOfferDto.getAction());
        savedJobOffer.setBackgroundUrl(manageJobOfferDto.getBackgroundUrl());
        savedJobOffer.setSalary(manageJobOfferDto.getSalary());
        savedJobOffer.setTags(manageJobOfferDto.getTags());
        savedJobOffer.setUser(user);
        if(savedJobOffer.getPromotionTier() <= 0){
            savedJobOffer.setVerified(false);
        }

        Category category = categoryService.getCategoryById(manageJobOfferDto.getCategoryId());
        if(category==null){
            return new CreateJobOfferResponse(HttpStatus.BAD_REQUEST, "", null);
        }
        savedJobOffer.setCategory(category);

        //Finds locations that have been deleted
        List<Location> locationsToRemove = new ArrayList<>(savedJobOffer.getLocations());
        locationsToRemove.removeAll(manageJobOfferDto.getLocations());

        //Saves new locations
        List<Location> locations = new ArrayList<>();
        for (Location location : manageJobOfferDto.getLocations()) {
            locations.add(locationRepository.save(location));
        }
        savedJobOffer.setLocations(locations);

        jobOfferRepository.deleteExperienceLevelsByJobOfferId(savedJobOffer.getId());
        for(Long id: manageJobOfferDto.getSelectedExperienceLevels()){
            ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelById(id);

            if(experienceLevel!=null){
                savedJobOffer.getExperienceLevels().add(experienceLevel);
            }
        }
        for(String name: manageJobOfferDto.getCustomExperienceLevels()){
            ExperienceLevel experienceLevel = experienceLevelService.addCustomExperienceLevel(name);

            savedJobOffer.getExperienceLevels().add(experienceLevel);
        }

        jobOfferRepository.deleteEmploymentTypesByJobOfferId(savedJobOffer.getId());
        for(Long id: manageJobOfferDto.getSelectedEmploymentTypes()){
            EmploymentType employmentType = employmentTypeService.getEmploymentTypeById(id);

            if(employmentType!=null){
                savedJobOffer.getEmploymentTypes().add(employmentType);
            }
        }
        for(String name: manageJobOfferDto.getCustomEmploymentTypes()){
            EmploymentType employmentType = employmentTypeService.addCustomEmploymentType(name);

            savedJobOffer.getEmploymentTypes().add(employmentType);
        }

        jobOfferRepository.deleteWorkTypesByJobOfferId(savedJobOffer.getId());
        for(Long id: manageJobOfferDto.getSelectedWorkTypes()){
            WorkType workType = workTypeService.getWorkTypeById(id);

            if(workType!=null){
                savedJobOffer.getWorkTypes().add(workType);
            }
        }
        for(String name: manageJobOfferDto.getCustomWorkTypes()){
            WorkType workType = workTypeService.addCustomWorkType(name);

            savedJobOffer.getWorkTypes().add(workType);
        }

        try {
            jobOfferRepository.save(savedJobOffer);

            locationRepository.deleteAll(locationsToRemove);
        } catch (DataIntegrityViolationException e) {
            return new CreateJobOfferResponse(HttpStatus.BAD_REQUEST, "Wystąpił błąd #323212. Zgłoś go Administracji.", null);
        }

        return new CreateJobOfferResponse(HttpStatus.OK, null, "/offer/" + savedJobOffer.getLinkUrl());
    }

    @Transactional
    public Response deleteJobOffer(String nameUrl) {
        JobOffer jobOffer = jobOfferRepository.findByLinkUrl(nameUrl).orElse(null);

        if(jobOffer==null){
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd podczas usuwania oferty. Jeśli błąd się powtórzy, proszę skontaktować się z Administracją strony.");
        }
        User user = userService.getLoggedUser();
        if(user == null){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Proszę się ponownie zalogować.");
        }
        if(user.getRole() != User.Role.ADMIN && jobOffer.getCompany()!=null){
            for(CompanyUserRole cur:jobOffer.getCompany().getCompanyUserRoles()){
                if(cur.getUser().equals(user)){
                    if(cur.getRole().value < 500){
                        return new Response(HttpStatus.BAD_REQUEST, "Nie posiadasz wymaganych uprawnień, aby usunąć ofertę tej firmy.");
                    }
                }
            }
        }

        if(jobOffer.getStatus() == JobOffer.Status.PUBLIC){
            Company company = jobOffer.getCompany();
            if(company != null && jobOffer.getCreatedAt() != null){
                long daysBetween = ChronoUnit.DAYS.between(jobOffer.getCreatedAt(), LocalDateTime.now());
                if(daysBetween<3){
                    company.changeTotalOffers(-1);
                    companyRepository.save(company);
                }
            }
        }

        jobOfferRepository.delete(jobOffer);

        for(WorkType workType:jobOffer.getWorkTypes()){
            if(workType.getId()>100){
                workTypeService.repository.delete(workType);
            }
        }
        for(ExperienceLevel experienceLevel:jobOffer.getExperienceLevels()){
            if(experienceLevel.getId()>100){
                experienceLevelService.repository.delete(experienceLevel);
            }
        }
        for(EmploymentType employmentType:jobOffer.getEmploymentTypes()){
            if(employmentType.getId()>100){
                employmentTypeService.repository.delete(employmentType);
            }
        }
        for(Location location:jobOffer.getLocations()){
            locationRepository.delete(location);
        }

        return new Response(HttpStatus.OK, null);
    }

    public JobOffer getPreviewJobOffer(ManageJobOfferDto manageJobOfferDto) {
        JobOffer jobOffer = new JobOffer();

        if(manageJobOfferDto.getCompanyId() != null && manageJobOfferDto.getCompanyId() != 0){
            Company company = companyService.getCompanyById(manageJobOfferDto.getCompanyId());
            if(company!=null){
                jobOffer.setCompany(company);
            }
        }
        else{
            SimpleCompany simpleCompany = manageJobOfferDto.getSimpleCompany();
            if(simpleCompany!=null){
                jobOffer.setSimpleCompany(simpleCompany);
            }
        }

        jobOffer.setTitle(manageJobOfferDto.getTitle());
        jobOffer.setDescription(manageJobOfferDto.getDescription());
        jobOffer.setAction(manageJobOfferDto.getAction());
        jobOffer.setBackgroundUrl(manageJobOfferDto.getBackgroundUrl());
        jobOffer.setSalary(manageJobOfferDto.getSalary());
        jobOffer.setTags(manageJobOfferDto.getTags());
        jobOffer.setPromotionTier(manageJobOfferDto.getPromotionTier());

        Category category = categoryService.getCategoryById(manageJobOfferDto.getCategoryId());
        if(category!=null){
            jobOffer.setCategory(category);
        }

        jobOffer.setLocations(manageJobOfferDto.getLocations());

        jobOffer.setCreatedAt(LocalDateTime.now());
        jobOffer.setRefreshedAt(LocalDate.now());
        jobOffer.setExpireAt(LocalDateTime.now().plusDays(31));

        for(Long id: manageJobOfferDto.getSelectedExperienceLevels()){
            ExperienceLevel experienceLevel = experienceLevelService.getExperienceLevelById(id);

            if(experienceLevel!=null){
                jobOffer.getExperienceLevels().add(experienceLevel);
            }
        }
        for(String name: manageJobOfferDto.getCustomExperienceLevels()){
            ExperienceLevel experienceLevel = new ExperienceLevel(name);

            jobOffer.getExperienceLevels().add(experienceLevel);
        }

        for(Long id: manageJobOfferDto.getSelectedEmploymentTypes()){
            EmploymentType employmentType = employmentTypeService.getEmploymentTypeById(id);

            if(employmentType!=null){
                jobOffer.getEmploymentTypes().add(employmentType);
            }
        }
        for(String name: manageJobOfferDto.getCustomEmploymentTypes()){
            EmploymentType employmentType = new EmploymentType(name);

            jobOffer.getEmploymentTypes().add(employmentType);
        }

        for(Long id: manageJobOfferDto.getSelectedWorkTypes()){
            WorkType workType = workTypeService.getWorkTypeById(id);

            if(workType!=null){
                jobOffer.getWorkTypes().add(workType);
            }
        }
        for(String name: manageJobOfferDto.getCustomWorkTypes()){
            WorkType workType = new WorkType(name);

            jobOffer.getWorkTypes().add(workType);
        }

        return jobOffer;
    }

    public Response changeVisibility(Long jobOfferId, JobOffer.Status status){
        User user = userService.getLoggedUser();
        if(user==null){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Zaloguj się ponownie");
        }

        JobOffer jobOffer = jobOfferRepository.findById(jobOfferId).orElse(null);
        if(jobOffer == null){
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd (#3341). Spróbuj ponownie lub skontaktuj się z Administracją!");
        }

        if(jobOffer.getStatus() == JobOffer.Status.ARCHIVED){
            return new Response(HttpStatus.OK, "Oferta jest już zarchiwizowana i nie można jej modyfikować");
        }

        Company company = jobOffer.getCompany();
        if(user.getRole()!= User.Role.ADMIN){
            if(company!=null){
                boolean foundUser = false;
                for(CompanyUserRole cur:company.getCompanyUserRoles()){
                    if(cur.getUser().equals(user)){
                        foundUser = true;
                        break;
                    }
                }

                if(!foundUser){
                    return new Response(HttpStatus.BAD_REQUEST, "Nie masz uprawnień, aby modyfikować tę wartość.");
                }
            }
            else{
                if(!jobOffer.getUser().equals(user)){
                    return new Response(HttpStatus.BAD_REQUEST, "Nie masz uprawnień, aby modyfikować tę wartość.");
                }
            }
        }

        if(jobOffer.getStatus() == JobOffer.Status.PRIVATE){
            if(status == JobOffer.Status.PUBLIC){
                LocalDateTime createdAt = jobOffer.getCreatedAt();
                if(createdAt == null){
                    jobOffer.setCreatedAt(LocalDateTime.now());
                    jobOffer.setRefreshedAt(LocalDate.now());
                    jobOffer.setExpireAt(LocalDateTime.now().plusDays(31));
                }
            }
        }

        jobOffer.setStatus(status);
        jobOfferRepository.save(jobOffer);
        return new Response(HttpStatus.OK, null);
    }

    public Response acceptOffer(Long jobOfferId){
        User user = userService.getLoggedUser();
        if(user==null || user.getRole() != User.Role.ADMIN){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Zaloguj się ponownie");
        }

        JobOffer jobOffer = jobOfferRepository.findById(jobOfferId).orElse(null);
        if(jobOffer == null){
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd (#2214). Spróbuj ponownie lub skontaktuj się z Administracją!");
        }

        jobOffer.setRejected(null);
        jobOffer.setVerified(true);
        if(jobOffer.getStatus() == JobOffer.Status.PUBLIC){
            LocalDateTime createdAt = jobOffer.getCreatedAt();
            if(createdAt == null){
                jobOffer.setCreatedAt(LocalDateTime.now());
                jobOffer.setRefreshedAt(LocalDate.now());
                jobOffer.setExpireAt(LocalDateTime.now().plusDays(31));
            }
        }
        Company company = jobOffer.getCompany();
        if(company != null){
            company.changeTotalOffers(1);
            companyRepository.save(company);
        }

        jobOfferRepository.save(jobOffer);
        return new Response(HttpStatus.OK, null);
    }

    public Response rejectOffer(Long jobOfferId, String reason) {
        User user = userService.getLoggedUser();
        if(user==null || user.getRole() != User.Role.ADMIN){
            return new Response(HttpStatus.UNAUTHORIZED, "Twoja sesja wygasła. Zaloguj się ponownie");
        }

        JobOffer jobOffer = jobOfferRepository.findById(jobOfferId).orElse(null);
        if(jobOffer == null){
            return new Response(HttpStatus.BAD_REQUEST, "Wystąpił nieoczekiwany błąd (#2214). Spróbuj ponownie lub skontaktuj się z Administracją!");
        }

        jobOffer.setRejected(reason);
        jobOffer.setVerified(false);
        jobOfferRepository.save(jobOffer);
        return new Response(HttpStatus.OK, null);
    }

    public void startPromotion(JobOfferPromotion jobOfferPromotion){
        JobOffer jobOffer = jobOfferRepository.findById(jobOfferPromotion.getJobOfferId()).orElse(null);
        if(jobOffer == null){
            System.out.println("Payment ERROR: #3215 "+jobOfferPromotion.getJobOfferId());
            return;
        }

        jobOffer.setRejected(null);
        jobOffer.setVerified(true);
        jobOffer.setRefreshedAt(LocalDate.now());
        jobOffer.setExpireAt(LocalDateTime.now().plusDays(31));
        jobOffer.setPromotionTier(jobOfferPromotion.getPromotionTier());

        jobOfferRepository.save(jobOffer);
    }
}
