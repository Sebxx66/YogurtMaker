package com.sebxx66.yogurtmaker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Solicitud para iniciar un lote")
public record StartBatchRequest(
        @Schema(description = "Receta a usar", example = "1") @NotNull(message = "La receta es obligatoria") Long recipeId,
        @Schema(description = "Litros de leche (opcional, usa el de la receta si no se envía)", example = "1.5")
        @Positive @DecimalMax("1000") Double customMilkVolume,
        @Schema(description = "Cucharadas de cultivo (opcional)", example = "2.0") @Positive Double customStarterAmount,
        @Schema(description = "Horas de incubación (opcional, entre el mínimo y el máximo de la receta)", example = "8")
        @Positive Integer customIncubationHours) {
}
