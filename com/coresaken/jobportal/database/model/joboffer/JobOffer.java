package com.coresaken.jobportal.database.model.joboffer;

import com.coresaken.jobportal.data.Salary;
import com.coresaken.jobportal.data.SimpleCompany;
import com.coresaken.jobportal.data.ApplicationAction;
import com.coresaken.jobportal.database.model.Company;
import com.coresaken.jobportal.database.model.Location;
import com.coresaken.jobportal.database.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "job_offer")
public class JobOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;

    @Transient
    SimpleCompany simpleCompany;

    @JsonIgnore
    @Column(name = "simple_company_json", columnDefinition = "TEXT")
    String simpleCompanyJson;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(length = 100, nullable = false)
    String title;

    @Column(length = 15000, nullable = false, columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    Status status;

    Boolean verified = false;

    @Column(length = 2000)
    String rejected;

    @Column
    String tags;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "promotion_tier")
    int promotionTier;

    String linkUrl;

    @Transient
    ApplicationAction action;

    @JsonIgnore
    String actionJson;

    @Transient
    List<Salary> salary;

    @JsonIgnore
    @Column(name = "salary", columnDefinition = "TEXT")
    String salaryJson;

    @Column(name = "background_url")
    String backgroundUrl;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    LocalDateTime createdAt;

    @Column(name = "refreshed_at", columnDefinition = "DATE")
    LocalDate refreshedAt;

    @Column(name = "expire_at", columnDefinition = "TIMESTAMP")
    LocalDateTime expireAt;

    @ManyToMany
    @JoinTable(
            name = "job_offer_location",
            joinColumns = @JoinColumn(name = "job_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    List<Location> locations = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "job_offer_experience_level",
            joinColumns = @JoinColumn(name = "job_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "experience_level_id")
    )
    List<ExperienceLevel> experienceLevels = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "job_offer_employment_type",
            joinColumns = @JoinColumn(name = "job_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "employment_type_id")
    )
    List<EmploymentType> employmentTypes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "job_offer_work_type",
            joinColumns = @JoinColumn(name = "job_offer_id"),
            inverseJoinColumns = @JoinColumn(name = "work_type_id")
    )
    List<WorkType> workTypes = new ArrayList<>();

    public enum Status {
        PUBLIC, PRIVATE, ARCHIVED
    }

    @PostPersist
    public void setLinkUrlAfterPersist() {
        String sanitizedTitle = sanitize(this.title);

        if(this.company!=null){
            this.linkUrl = this.id + "-" + sanitizedTitle + "-for-" + sanitize(this.company.getName());
        }
        else if(this.simpleCompany!=null && !this.simpleCompany.getName().isEmpty()){
            this.linkUrl = this.id + "-" + sanitizedTitle + "-for-" + sanitize(this.simpleCompany.getName());
        }
        else{
            this.linkUrl = this.id + "-" + sanitizedTitle;
        }
    }

    @PostLoad
    private void loadJsonData() {
        if (simpleCompanyJson != null && !simpleCompanyJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                simpleCompany = objectMapper.readValue(simpleCompanyJson, SimpleCompany.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.salaryJson != null && !salaryJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.salary = objectMapper.readValue(this.salaryJson, new TypeReference<List<Salary>>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.actionJson != null && !actionJson.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.action = objectMapper.readValue(this.actionJson, new TypeReference<ApplicationAction>(){});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PrePersist
    @PreUpdate
    private void saveJsonData() {
        if (simpleCompany != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                simpleCompanyJson = objectMapper.writeValueAsString(simpleCompany);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else{
            simpleCompanyJson = null;
        }

        if (this.salary != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.salaryJson = objectMapper.writeValueAsString(this.salary);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else{
            salaryJson = null;
        }

        if (this.action != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                this.actionJson = objectMapper.writeValueAsString(this.action);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else{
            salaryJson = null;
        }
    }

    private String sanitize(String text){
        text = text.replaceAll("[ ,./@_]", "-");
        text = text.replaceAll("[^a-zA-Z0-9-]", "");
        text = text.toLowerCase();
        return text;
    }
}
