package com.coresaken.jobportal.database.model;

import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true)
    String name;

    @Column(length = 15000, columnDefinition = "TEXT")
    String description;

    @Column(name = "logo_url")
    String logoUrl;

    @Column(name = "background_url")
    String backgroundUrl;

    @Column(name = "link_url", unique = true)
    String linkUrl;

    int totalOffers;

    @ManyToMany
            @JoinTable(
                    name = "company_link",
                    joinColumns = @JoinColumn(name = "company_id"),
                    inverseJoinColumns = @JoinColumn(name = "link_id")
            )
    List<Link> links = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "company_location",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    List<Location> locations = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "user_company_follow",
            joinColumns = @JoinColumn(name = "company_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    Set<User> followedUsers = new HashSet<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    Set<CompanyUserRole> companyUserRoles = new HashSet<>();

    @JsonBackReference
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    Set<JobOffer> jobOffers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company city = (Company) o;
        return Objects.equals(id, city.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void changeTotalOffers(int i){
        totalOffers += i;
        if(totalOffers < 0){
            totalOffers = 0;
        }
    }
}
