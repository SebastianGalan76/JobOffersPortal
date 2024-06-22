package com.coresaken.jobportal.controller;

import com.coresaken.jobportal.data.dto.SearchDto;
import com.coresaken.jobportal.database.model.City;
import com.coresaken.jobportal.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CityController {
    final CityService cityService;

    @ResponseBody
    @PostMapping("/city/search")
    public List<City> getCitiesByName(@RequestBody SearchDto searchDto){
        String search = searchDto.getValue();

        if(search == null || search.isEmpty()){
            return cityService.getMostPopularCities();
        }

        return cityService.findCityByName(search);
    }
}
