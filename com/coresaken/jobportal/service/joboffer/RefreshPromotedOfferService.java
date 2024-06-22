package com.coresaken.jobportal.service.joboffer;

import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.coresaken.jobportal.database.repository.joboffer.JobOfferRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class RefreshPromotedOfferService {
    final JobOfferRepository repository;

    @Scheduled(fixedRate = 21600000) //6h
    public void executeTask() {
        LocalDate currentDate = LocalDate.now();
        List<JobOffer> promotedJobOffers = repository.findAllByStatusNotAndPromotionTierGreaterThan(JobOffer.Status.ARCHIVED, 0);

        for (JobOffer jobOffer : promotedJobOffers) {
            int timePeriod = getTimePeriodForPromotionTier(jobOffer.getPromotionTier());
            if(timePeriod==-1){
                continue;
            }

            LocalDate lastRefreshedAt = jobOffer.getRefreshedAt();
            if(lastRefreshedAt==null){
                continue;
            }

            LocalDate daysAgo = currentDate.minusDays(timePeriod);
            if(lastRefreshedAt.isBefore(daysAgo) || lastRefreshedAt.isEqual(daysAgo)){
                jobOffer.setRefreshedAt(currentDate);
                repository.save(jobOffer);
            }
        }
    }

    private int getTimePeriodForPromotionTier(int promotionTier){
        if(promotionTier == 1){
            return 7;
        }
        else{
            return -1;
        }
    }
}
