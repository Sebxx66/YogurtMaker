package com.sebxx66.yogurtmaker.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.sebxx66.yogurtmaker.dto.IngredientRequest;
import com.sebxx66.yogurtmaker.dto.RecipeRequest;
import com.sebxx66.yogurtmaker.model.Recipe.DifficultyLevel;
import com.sebxx66.yogurtmaker.repository.RecipeRepository;
import com.sebxx66.yogurtmaker.service.RecipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Carga dos recetas de ejemplo al arrancar si la base está vacía ({@code yogurt.demo-data=true}). */
@Component
@ConditionalOnProperty(prefix = "yogurt", name = "demo-data", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class DemoDataLoader implements ApplicationRunner {

    private final RecipeRepository recipeRepository;
    private final RecipeService recipeService;

    @Override
    public void run(ApplicationArguments args) {
        if (recipeRepository.count() > 0) {
            return;
        }
        recipeService.create(new RecipeRequest(
                "Yogurt natural", "Yogurt clásico, suave y cremoso", 2.0, 2.0,
                85.0, 10, 45.0, 43.0, 6, 10, 4, DifficultyLevel.BEGINNER,
                "Usa leche entera y no muevas el recipiente durante la incubación",
                List.of(new IngredientRequest("Leche entera", 2.0, "L", null, false),
                        new IngredientRequest("Yogurt natural (cultivo)", 2.0, "cucharadas", "Con cultivos vivos", false))));
        recipeService.create(new RecipeRequest(
                "Yogurt griego", "Yogurt espeso que se cuela después de refrigerar", 3.0, 3.0,
                90.0, 15, 46.0, 42.0, 8, 12, 6, DifficultyLevel.INTERMEDIATE,
                "Cuélalo con una tela fina durante 2 a 4 horas en la nevera",
                List.of(new IngredientRequest("Leche entera", 3.0, "L", null, false),
                        new IngredientRequest("Yogurt griego (cultivo)", 3.0, "cucharadas", null, false),
                        new IngredientRequest("Leche en polvo", 50.0, "g", "Da más cuerpo", true))));
        log.info("Se cargaron 2 recetas de ejemplo");
    }
}
