package com.sebxx66.yogurtmaker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Ingrediente de una receta")
public record IngredientRequest(
        @Schema(example = "Leche entera") @NotBlank(message = "El nombre es obligatorio") @Size(max = 100) String name,
        @Schema(example = "1.0") @Positive(message = "La cantidad debe ser mayor que cero") Double quantity,
        @Schema(example = "L") @Size(max = 20) String unit,
        @Schema(example = "Mejor si es fresca") @Size(max = 255) String notes,
        @Schema(description = "Si es opcional", example = "false") Boolean optional) {
}
