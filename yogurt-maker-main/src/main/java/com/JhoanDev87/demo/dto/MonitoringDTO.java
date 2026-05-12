package com.JhoanDev87.demo.dto;

import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

public class MonitoringDTO {
    
    @Data
    @Builder
    @Schema(description = "Resumen de temperatura para un lote de yogurt")
    public static class TemperatureSummary {
        @Schema(description = "Temperatura actual del lote en °C", example = "42.0")
        private Double currentTemperature;

        @Schema(description = "Temperatura máxima registrada en °C", example = "43.5")
        private Double maximumTemperature;

        @Schema(description = "Temperatura mínima registrada en °C", example = "40.0")
        private Double minimumTemperature;

        @Schema(description = "Temperatura promedio de incubación en °C", example = "42.1")
        private Double averageTemperature;
    }
    
    @Data
    @Builder
    @Schema(description = "Panel de control con métricas y conteos de lotes")
    public static class Dashboard {
        @Schema(description = "Conteo de lotes por estado")
        private Map<String, Long> batchCounts;

        @Schema(description = "Cantidad de lotes activos")
        private Long activeBatchesCount;

        @Schema(description = "Número de lotes completados hoy")
        private Integer completedToday;
    }
}

