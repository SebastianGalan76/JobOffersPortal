package com.coresaken.jobportal.database.model.joboffer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employment_type")
public class EmploymentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 40)
    String employmentType;

    @JsonIgnore
    @ManyToMany(mappedBy = "employmentTypes")
    private Set<JobOffer> jobOffers = new HashSet<>();

    public EmploymentType(String name){
        employmentType = name;
    }
}
