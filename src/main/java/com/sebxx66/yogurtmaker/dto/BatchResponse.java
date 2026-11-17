package com.sebxx66.yogurtmaker.dto;

import java.time.LocalDateTime;

import com.sebxx66.yogurtmaker.model.BatchStatus;
import com.sebxx66.yogurtmaker.model.YogurtBatch;

public record BatchResponse(
        Long id,
        String batchCode,
        Long recipeId,
        String recipeName,
        BatchStatus status,
        Double milkVolume,
        Double starterAmount,
        Double targetTemperature,
        Integer incubationTime,
        LocalDateTime startTime,
        LocalDateTime incubationStartTime,
        LocalDateTime incubationEndTime,
        LocalDateTime refrigerationStartTime,
        LocalDateTime completedAt,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static BatchResponse from(YogurtBatch batch) {
        return new BatchResponse(batch.getId(), batch.getBatchCode(), batch.getRecipe().getId(),
                batch.getRecipe().getName(), batch.getStatus(), batch.getMilkVolume(), batch.getStarterAmount(),
                batch.getTargetTemperature(), batch.getIncubationTime(), batch.getStartTime(),
                batch.getIncubationStartTime(), batch.getIncubationEndTime(), batch.getRefrigerationStartTime(),
                batch.getCompletedAt(), batch.getNotes(), batch.getCreatedAt(), batch.getUpdatedAt());
    }
}
