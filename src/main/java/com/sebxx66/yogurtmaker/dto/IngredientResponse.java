package com.sebxx66.yogurtmaker.dto;

import com.sebxx66.yogurtmaker.model.Ingredient;

public record IngredientResponse(Long id, String name, Double quantity, String unit, String notes, boolean optional) {

    public static IngredientResponse from(Ingredient ingredient) {
        return new IngredientResponse(ingredient.getId(), ingredient.getName(), ingredient.getQuantity(),
                ingredient.getUnit(), ingredient.getNotes(), Boolean.TRUE.equals(ingredient.getOptional()));
    }
}
