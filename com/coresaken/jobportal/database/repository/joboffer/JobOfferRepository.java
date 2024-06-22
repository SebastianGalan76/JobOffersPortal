package com.coresaken.jobportal.database.repository.joboffer;

import com.coresaken.jobportal.database.model.User;
import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {
    @Query("SELECT jo FROM JobOffer jo WHERE jo.category.id IN :categoryIds")
    List<JobOffer> findByCategoryIds(@Param("categoryIds") Set<Long> categoryIds);

    @Query("SELECT DISTINCT jo FROM JobOffer jo " +
            "LEFT JOIN jo.locations loc " +
            "LEFT JOIN loc.city city " +
            "LEFT JOIN jo.workTypes wt " +
            "LEFT JOIN jo.employmentTypes et " +
            "LEFT JOIN jo.experienceLevels el " +
            "WHERE jo.category.id IN :categoryIds " +
            "AND (:cityIds IS NULL OR city.id IN :cityIds) " +
            "AND (" +
            "   (:workTypeIds IS NULL OR wt.id IN :workTypeIds) " +
            "   OR (-1 IN :workTypeIds AND wt.id >= 100) " +
            ") " +
            "AND (" +
            "   (:employmentTypeIds IS NULL OR et.id IN :employmentTypeIds) " +
            "   OR (-1 IN :employmentTypeIds AND et.id >= 100) " +
            ") " +
            "AND (" +
            "   (:experienceLevelIds IS NULL OR el.id IN :experienceLevelIds) " +
            "   OR (-1 IN :experienceLevelIds AND el.id >= 100) " +
            ") "+
            "AND jo.status = :status " +
            "AND jo.verified = true")
    Page<JobOffer> getJobOffers(@Param("categoryIds") Set<Long> categoryIds,
                                @Param("cityIds") Set<Long> cityIds,
                                @Param("workTypeIds") Set<Long> workTypeIds,
                                @Param("employmentTypeIds") Set<Long> employmentTypeIds,
                                @Param("experienceLevelIds") Set<Long> experienceLevelIds,
                                @Param("status") JobOffer.Status status,
                                Pageable pageable);

    Optional<JobOffer> findByLinkUrl(String linkUrl);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM job_offer_experience_level WHERE job_offer_id = :jobOfferId", nativeQuery = true)
    void deleteExperienceLevelsByJobOfferId(@Param("jobOfferId") Long jobOfferId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM job_offer_employment_type WHERE job_offer_id = :jobOfferId", nativeQuery = true)
    void deleteEmploymentTypesByJobOfferId(@Param("jobOfferId") Long jobOfferId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM job_offer_work_type WHERE job_offer_id = :jobOfferId", nativeQuery = true)
    void deleteWorkTypesByJobOfferId(@Param("jobOfferId") Long jobOfferId);

    @Query("SELECT j FROM JobOffer j WHERE j.user = :loggedInUser " +
            "OR j.company IN (" +
            "SELECT cur.company FROM CompanyUserRole cur " +
            "WHERE cur.user = :loggedInUser AND cur.role IN (com.coresaken.jobportal.database.model.CompanyUserRole$Role.ADMINISTRATOR, com.coresaken.jobportal.database.model.CompanyUserRole$Role.MODERATOR, com.coresaken.jobportal.database.model.CompanyUserRole$Role.RECRUITER))")
    List<JobOffer> findAllJobOffersForUser(@Param("loggedInUser") User loggedInUser);

    @Query("SELECT j FROM JobOffer j WHERE j.verified = false and j.status != com.coresaken.jobportal.database.model.joboffer.JobOffer$Status.ARCHIVED")
    List<JobOffer> findAllUnverifiedOffers();

    List<JobOffer> findByExpireAtBeforeAndStatusNot(LocalDateTime currentDate, JobOffer.Status status);

    @Query("SELECT j FROM JobOffer j WHERE j.status != :status AND j.promotionTier > :promotionTier")
    List<JobOffer> findAllByStatusNotAndPromotionTierGreaterThan(@Param("status") JobOffer.Status status, @Param("promotionTier") int promotionTier);
}
