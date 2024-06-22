package com.coresaken.jobportal.database.model;

import com.coresaken.jobportal.database.model.key.CompanyUserRoleKey;
import com.coresaken.jobportal.serializer.CompanyUserRoleSerializer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;


@JsonSerialize(using = CompanyUserRoleSerializer.class)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company_user_role")
public class CompanyUserRole {
    @EmbeddedId
    CompanyUserRoleKey id;

    @ManyToOne
    @MapsId("companyId")
    @JoinColumn(name = "company_id")
    Company company;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @Enumerated(EnumType.STRING)
    Role role;

    @Getter
    public enum Role{
        RECRUITER(500),
        MODERATOR(750),
        ADMINISTRATOR(1000),
        ;

        public final int value;

        Role(int value){
            this.value = value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyUserRole cur = (CompanyUserRole) o;
        return Objects.equals(id.getCompanyId(), cur.getId().getCompanyId()) && Objects.equals(id.getUserId(), cur.getId().getCompanyId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
