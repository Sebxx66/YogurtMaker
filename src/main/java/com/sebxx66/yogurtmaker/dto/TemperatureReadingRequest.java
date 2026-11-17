package com.sebxx66.yogurtmaker.dto;

import com.sebxx66.yogurtmaker.model.TemperatureLog.LogType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Lectura de temperatura registrada manualmente")
public record TemperatureReadingRequest(
        @Schema(description = "°C", example = "42.8") @NotNull @DecimalMin("-30") @DecimalMax("120") Double temperature,
        @Schema(description = "Fase de la lectura; MANUAL si no se envía") LogType type,
        @Schema(example = "Medido con termómetro de cocina") @Size(max = 255) String notes) {
}
