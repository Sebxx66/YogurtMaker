package com.sebxx66.yogurtmaker.dto;

import java.time.LocalDateTime;

import com.sebxx66.yogurtmaker.model.TemperatureLog;
import com.sebxx66.yogurtmaker.model.TemperatureLog.LogType;

public record TemperatureLogResponse(Long id, Double temperature, LogType type, LocalDateTime recordedAt, String notes) {

    public static TemperatureLogResponse from(TemperatureLog log) {
        return new TemperatureLogResponse(log.getId(), log.getTemperature(), log.getType(), log.getRecordedAt(),
                log.getNotes());
    }
}
