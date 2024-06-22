package com.coresaken.jobportal.service.joboffer;

import com.coresaken.jobportal.database.model.joboffer.JobOffer;
import com.coresaken.jobportal.database.repository.joboffer.JobOfferRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ArchiveExpiredOfferService {

    final JobOfferRepository jobOfferRepository;

    @Scheduled(fixedRate = 10800000) //3h
    public void executeTask() {
        LocalDateTime currentDate = LocalDateTime.now();
        List<JobOffer> expiredJobOffers = jobOfferRepository.findByExpireAtBeforeAndStatusNot(currentDate, JobOffer.Status.ARCHIVED);

        for (JobOffer jobOffer : expiredJobOffers) {
            jobOffer.setStatus(JobOffer.Status.ARCHIVED);
            jobOfferRepository.save(jobOffer);
        }
    }
}