package com.coresaken.jobportal.service.joboffer;

import com.coresaken.jobportal.database.model.joboffer.ExperienceLevel;
import com.coresaken.jobportal.database.repository.joboffer.ExperienceLevelRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ExperienceLevelService {
    final ExperienceLevelRepository repository;

    List<ExperienceLevel> experienceLevels;

    @PostConstruct
    public void initialize(){
        experienceLevels = repository.findAll();
    }

    public ExperienceLevel getExperienceLevelById(Long id){
        for (ExperienceLevel experienceLevel: experienceLevels) {
            if(Objects.equals(experienceLevel.getId(), id)){
                return experienceLevel;
            }
        }
        return null;
    }

    public ExperienceLevel addCustomExperienceLevel(String name){
        return repository.save(new ExperienceLevel(name));
    }
}
