package com.kbc.kibi_coins.util;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.repository.CategoryRepository;
import com.kbc.kibi_coins.repository.CommentCategoryMappingRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
public class CategoryMapping {
    private Map<String, Category> categoryMappings = new HashMap<>();
    private List<String> transportationList;
    private List<String> foodList;
    private List<String> entertainmentList;
    private List<String> healthcareList;
    private List<String> clothingList;
    private List<String> educationList;
    private List<String> suppliesList;
    private List<String> sportsList;

    public void setCategoryMappings(Map<String, Category> categoryMappings) {
        this.categoryMappings = categoryMappings;
    }

    @PostConstruct
    private void initComments() {
        this.transportationList = List.of(
                "OMV", "Tfs Petrol 7116", "Skoda", "Wizz Air",
                "Wizzairecombgnusd", "Ryanair", "Etihad Airways",
                "LUKOIL", "Sofia Transit", "Shell"
        );
        this.foodList = List.of(
                "Aleks 1850 Eood", "BETA", "Aladin Foods", "Aleks Treyd 2010",
                "Minimart", "Multi Kuki Ood", "Lidl", "Kaufland", "Bluestone Doughnuts",
                "The Hungry Griffin", "Ma Baker", "Divis 60 Eood", "BILLA", "Karaenev Trading Eood",
                "Goldfingers", "Fuud Grup Bg Eood", "Open Kitchen Ad", "Fantastico", "Kamenitsa",
                "Altruist Urban Cafe", "Monro Plovdiv"
        );
        this.entertainmentList = List.of(
                "Barber&Cat Bar", "Klub Sinema Ood", "Biy Bop Eood", "Plovdivfilm Ood",
                "Zhanet 2001 Eood"
        );
        this.healthcareList = List.of(
                "SOpharmacy", "Inaessentials Store", "Aromati Drogeriya", "dm drogerie"
        );
        this.clothingList = List.of(
                "obuvki.bg"
        );
        this.educationList = List.of(
                "Prosveta Sofia Ad", "Epay Arc.academy"
        );
        this.suppliesList = List.of(
                "PEPCO", "IKEA", "English home", "Phoenix Light"
        );
        this.sportsList = List.of("Decathlon");
    }

}
