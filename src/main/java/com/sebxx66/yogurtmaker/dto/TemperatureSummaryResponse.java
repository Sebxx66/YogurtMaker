package com.sebxx66.yogurtmaker.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de temperatura de un lote")
public record TemperatureSummaryResponse(
        @Schema(description = "Última lectura registrada (°C)") Double currentTemperature,
        @Schema(description = "Mínima durante la incubación (°C)") Double minimumIncubationTemperature,
        @Schema(description = "Máxima durante la incubación (°C)") Double maximumIncubationTemperature,
        @Schema(description = "Promedio durante la incubación (°C)") Double averageIncubationTemperature,
        @Schema(description = "Lecturas tomadas durante la incubación") long incubationReadings) {
}
