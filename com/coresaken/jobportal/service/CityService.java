package com.coresaken.jobportal.service;

import com.coresaken.jobportal.database.model.City;
import com.coresaken.jobportal.database.repository.CityRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Data
@Service
@RequiredArgsConstructor
public class CityService {
    final CityRepository cityRepository;

    List<City> cities;
    List<City> mostPopularCities;

    @PostConstruct
    public void initialize(){
        cities = cityRepository.findAll();

        mostPopularCities = new ArrayList<>();
        int[] mostPopularCitiesIds = new int[]{0,1,2,3,4,5,6,7};
        for(int id:mostPopularCitiesIds){
            if(id<cities.size()){
                mostPopularCities.add(cities.get(id));
            }
        }
    }

    public List<City> findCityByName(String name){
        List<City> foundedCity = new ArrayList<>();

        for(City city:cities){
            if(city.getName().toLowerCase().startsWith(name.toLowerCase())){
                foundedCity.add(city);
            }
        }

        return foundedCity;
    }

    public List<City> getMostPopularCity(){
        return mostPopularCities;
    }
}
