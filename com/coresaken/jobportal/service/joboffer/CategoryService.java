package com.coresaken.jobportal.service.joboffer;

import com.coresaken.jobportal.database.model.joboffer.Category;
import com.coresaken.jobportal.database.repository.joboffer.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Data
@Service
@AllArgsConstructor
public class CategoryService {
    final CategoryRepository categoryRepository;

    List<Category> categories;

    @PostConstruct
    public void initialize(){
        categories = categoryRepository.findAll();
    }

    public Category getCategoryById(Long id){
        for (Category category: categories) {
            if(Objects.equals(category.getId(), id)){
                return category;
            }
        }
        return null;
    }
}


