package com.sebxx66.yogurtmaker.dto;

import java.util.Map;

import com.sebxx66.yogurtmaker.model.BatchStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Métricas generales de producción")
public record DashboardResponse(
        @Schema(description = "Cantidad de lotes por estado") Map<BatchStatus, Long> batchCounts,
        @Schema(description = "Lotes que siguen en proceso") long activeBatches,
        @Schema(description = "Lotes terminados hoy") long completedToday) {
}
