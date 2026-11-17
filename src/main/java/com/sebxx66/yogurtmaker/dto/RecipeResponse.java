package com.sebxx66.yogurtmaker.dto;

import java.util.List;

import com.sebxx66.yogurtmaker.model.Recipe;
import com.sebxx66.yogurtmaker.model.Recipe.DifficultyLevel;

public record RecipeResponse(
        Long id,
        String name,
        String description,
        Double defaultMilkVolume,
        Double defaultStarterAmount,
        Double heatingTemperature,
        Integer heatingDuration,
        Double inoculationTemperature,
        Double incubationTemperature,
        Integer minIncubationTime,
        Integer maxIncubationTime,
        Integer refrigerationTime,
        DifficultyLevel difficulty,
        String tips,
        boolean active,
        List<IngredientResponse> ingredients) {

    public static RecipeResponse from(Recipe recipe) {
        return new RecipeResponse(recipe.getId(), recipe.getName(), recipe.getDescription(),
                recipe.getDefaultMilkVolume(), recipe.getDefaultStarterAmount(), recipe.getHeatingTemperature(),
                recipe.getHeatingDuration(), recipe.getInoculationTemperature(), recipe.getIncubationTemperature(),
                recipe.getMinIncubationTime(), recipe.getMaxIncubationTime(), recipe.getRefrigerationTime(),
                recipe.getDifficulty(), recipe.getTips(), Boolean.TRUE.equals(recipe.getActive()),
                recipe.getIngredients().stream().map(IngredientResponse::from).toList());
    }
}
