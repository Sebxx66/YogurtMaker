package com.sebxx66.yogurtmaker.dto;

import java.util.List;

import com.sebxx66.yogurtmaker.model.Recipe.DifficultyLevel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos para crear o actualizar una receta")
public record RecipeRequest(
        @Schema(example = "Yogurt natural") @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String name,
        @Schema(example = "Yogurt cremoso con cultivo natural") @Size(max = 1000) String description,
        @Schema(description = "Litros de leche", example = "2.0") @NotNull @Positive @DecimalMax("1000") Double defaultMilkVolume,
        @Schema(description = "Cucharadas de cultivo", example = "2.0") @NotNull @Positive Double defaultStarterAmount,
        @Schema(description = "°C", example = "85") @NotNull @DecimalMin("60") @DecimalMax("100") Double heatingTemperature,
        @Schema(description = "Minutos", example = "10") @NotNull @Positive @Max(120) Integer heatingDuration,
        @Schema(description = "°C", example = "45") @NotNull @DecimalMin("30") @DecimalMax("55") Double inoculationTemperature,
        @Schema(description = "°C", example = "43") @NotNull @DecimalMin("30") @DecimalMax("50") Double incubationTemperature,
        @Schema(description = "Horas", example = "6") @NotNull @Positive @Max(48) Integer minIncubationTime,
        @Schema(description = "Horas", example = "10") @NotNull @Positive @Max(48) Integer maxIncubationTime,
        @Schema(description = "Horas", example = "4") @NotNull @Positive @Max(72) Integer refrigerationTime,
        @NotNull(message = "La dificultad es obligatoria") DifficultyLevel difficulty,
        @Schema(example = "Usa leche entera para más cremosidad") @Size(max = 1000) String tips,
        @Valid List<IngredientRequest> ingredients) {
}
