package com.coresaken.jobportal.database;

import com.coresaken.jobportal.database.model.City;
import com.coresaken.jobportal.database.model.joboffer.Category;
import com.coresaken.jobportal.database.model.joboffer.EmploymentType;
import com.coresaken.jobportal.database.model.joboffer.ExperienceLevel;
import com.coresaken.jobportal.database.model.joboffer.WorkType;
import com.coresaken.jobportal.database.repository.CityRepository;
import com.coresaken.jobportal.database.repository.joboffer.CategoryRepository;
import com.coresaken.jobportal.database.repository.joboffer.EmploymentTypeRepository;
import com.coresaken.jobportal.database.repository.joboffer.ExperienceLevelRepository;
import com.coresaken.jobportal.database.repository.joboffer.WorkTypeRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@Component
public class DefaultDataLoader implements CommandLineRunner {
    final CategoryRepository categoryRepository;
    final ExperienceLevelRepository experienceLevelRepository;
    final EmploymentTypeRepository employmentTypeRepository;
    final WorkTypeRepository workTypeRepository;
    final CityRepository cityRepository;

    @Override
    public void run(String... args) throws Exception {
        loadDefaultData();
    }

    private void loadDefaultData(){
        loadCategories();
        loadExperienceLevels();
        loadEmploymentTypes();
        loadWorkTypes();
        loadCities();
    }

    private void loadCategories(){
        if(categoryRepository.count() > 0){
            return;
        }

        String[] defaultValues = {"Programowanie", "Grafika i animacje", "Dźwięk i muzyka", "Testowanie",
        "Projektowanie", "Produkcja i zarządzanie", "Marketing i PR", "Obsługa techniczna i wsparcie", "Inne"};
        for(String value:defaultValues){
            categoryRepository.save(new Category(value));
        }
        for(int i=defaultValues.length;i<100;i++){
            categoryRepository.save(new Category("null"));
        }
    }
    private void loadEmploymentTypes(){
        if(employmentTypeRepository.count() > 0){
            return;
        }

        String[] defaultValues = {"Umowa o pracę", "Umowa zlecenie", "Kontrakt B2B", "Staż", "Praktyki"};
        for(String value:defaultValues){
            employmentTypeRepository.save(new EmploymentType(value));
        }
        for(int i=defaultValues.length;i<100;i++){
            employmentTypeRepository.save(new EmploymentType("null"));
        }
    }
    private void loadExperienceLevels(){
        if(experienceLevelRepository.count() > 0){
            return;
        }

        String[] defaultValues = {"Praktykant", "Stażysta", "Młodszy specjalista (Junior)", "Specjalista (Regular)", "Starszy specjalista (Senior)", "Główny specjalista (Principal)"};
        for(String value:defaultValues){
            experienceLevelRepository.save(new ExperienceLevel(value));
        }
        for(int i=defaultValues.length;i<100;i++){
            experienceLevelRepository.save(new ExperienceLevel("null"));
        }
    }
    private void loadWorkTypes(){
        if(workTypeRepository.count() > 0){
            return;
        }

        String[] defaultValues = {"Praca stacjonarna", "Praca hybrydowa", "Praca zdalna", "Pełny etat", "Niepełny etat"};
        for(String value:defaultValues){
            workTypeRepository.save(new WorkType(value));
        }
        for(int i=defaultValues.length;i<100;i++){
            workTypeRepository.save(new WorkType("null"));
        }
    }
    private void loadCities(){
        if(cityRepository.count() > 0){
            return;
        }

        String[] defaultValues = {"Warszawa", "Kraków", "Gdańsk", "Poznań", "Katowice", "Szczecin", "Lublin", "Wrocław", "Białystok", "Łódź", "Bydgoszcz", "Gdynia", "Częstochowa", "Radom", "Sosnowiec", "Toruń", "Kielce", "Rzeszów", "Gliwice", "Zabrze", "Olsztyn", "Bielsko-Biała", "Bytom", "Zielona Góra", "Rybnik", "Ruda Śląska", "Opole", "Tychy", "Gorzów Wielkopolski", "Dąbrowa Górnicza", "Płock", "Elbląg", "Wałbrzych", "Włocławek", "Tarnów", "Chorzów", "Koszalin", "Kalisz", "Legnica", "Grudziądz", "Słupsk", "Jaworzno", "Jastrzębie-Zdrój", "Nowy Sącz", "Jelenia Góra", "Konin", "Piotrków Trybunalski", "Lubin", "Inowrocław", "Suwałki", "Mysłowice", "Ostrowiec Świętokrzyski", "Siemianowice Śląskie", "Gniezno", "Głogów", "Zamość", "Chełm", "Leszno", "Tomaszów Mazowiecki", "Przemyśl", "Stalowa Wola", "Kędzierzyn-Koźle", "Łomża", "Żory", "Tarnowskie Góry", "Pabianice", "Świdnica", "Biała Podlaska", "Ełk", "Pruszków", "Ostrołęka", "Stargard", "Legionowo", "Tarnobrzeg", "Puławy", "Racibórz", "Wejherowo", "Radomsko", "Skierniewice", "Starachowice", "Kutno", "Siedlce", "Nysa", "Mielec", "Piła", "Ostrów Wielkopolski", "Lubartów", "Jarosław", "Malbork", "Kraśnik", "Nowa Sól", "Zgierz", "Kołobrzeg", "Będzin", "Otwock", "Swarzędz", "Knurów", "Bochnia", "Świętochłowice", "Lębork"};
        for(String value:defaultValues){
            cityRepository.save(new City(value));
        }
    }
}
