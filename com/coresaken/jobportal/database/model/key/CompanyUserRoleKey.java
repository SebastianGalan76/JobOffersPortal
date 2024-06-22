package com.coresaken.jobportal.database.model.key;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class CompanyUserRoleKey implements Serializable {
    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "user_id")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompanyUserRoleKey that = (CompanyUserRoleKey) o;

        if (!Objects.equals(companyId, that.companyId)) return false;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        int result = companyId != null ? companyId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }
}
