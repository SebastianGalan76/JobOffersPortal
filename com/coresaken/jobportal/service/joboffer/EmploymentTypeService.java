package com.coresaken.jobportal.service.joboffer;

import com.coresaken.jobportal.database.model.joboffer.EmploymentType;
import com.coresaken.jobportal.database.repository.joboffer.EmploymentTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EmploymentTypeService {
    final EmploymentTypeRepository repository;

    List<EmploymentType> employmentTypes;

    @PostConstruct
    public void initialize(){
        employmentTypes = repository.findAll();
    }

    public EmploymentType getEmploymentTypeById(Long id){
        for (EmploymentType employmentType: employmentTypes) {
            if(Objects.equals(employmentType.getId(), id)){
                return employmentType;
            }
        }
        return null;
    }

    public EmploymentType addCustomEmploymentType(String name){
        return repository.save(new EmploymentType(name));
    }
}
