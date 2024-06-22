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
@Table(name = "experience_level")
public class ExperienceLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 40)
    String experienceLevel;

    @JsonIgnore
    @ManyToMany(mappedBy = "experienceLevels")
    private Set<JobOffer> jobOffers = new HashSet<>();

    public ExperienceLevel(String name){
        experienceLevel = name;
    }
}
