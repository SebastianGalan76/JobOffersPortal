package com.coresaken.jobportal.service.joboffer;

import com.coresaken.jobportal.database.model.joboffer.EmploymentType;
import com.coresaken.jobportal.database.model.joboffer.WorkType;
import com.coresaken.jobportal.database.repository.joboffer.WorkTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class WorkTypeService {
    final WorkTypeRepository repository;

    List<WorkType> workTypes;

    @PostConstruct
    public void initialize(){
        workTypes = repository.findAll();
    }

    public WorkType getWorkTypeById(Long id){
        for (WorkType workType: workTypes) {
            if(Objects.equals(workType.getId(), id)){
                return workType;
            }
        }
        return null;
    }

    public WorkType addCustomWorkType(String name){
        return repository.save(new WorkType(name));
    }
}
