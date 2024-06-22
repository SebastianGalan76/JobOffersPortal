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
@Table(name = "work_type")
public class WorkType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 40)
    String workType;

    @JsonIgnore
    @ManyToMany(mappedBy = "workTypes")
    private Set<JobOffer> jobOffers = new HashSet<>();

    public WorkType(String name){
        this.workType = name;
    }
}
