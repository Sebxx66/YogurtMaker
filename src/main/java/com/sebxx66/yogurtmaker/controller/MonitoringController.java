package com.sebxx66.yogurtmaker.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sebxx66.yogurtmaker.dto.BatchResponse;
import com.sebxx66.yogurtmaker.dto.DashboardResponse;
import com.sebxx66.yogurtmaker.dto.TemperatureLogResponse;
import com.sebxx66.yogurtmaker.dto.TemperatureSummaryResponse;
import com.sebxx66.yogurtmaker.service.MonitoringService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/monitoring")
@Tag(name = "Monitoreo", description = "Estado de la producción y temperaturas")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    @Operation(summary = "Lotes en proceso", description = "Todos los lotes que no están COMPLETED ni FAILED")
    @GetMapping("/batches/active")
    public List<BatchResponse> activeBatches() {
        return monitoringService.activeBatches();
    }

    @Operation(summary = "Resumen de temperatura", description = "Última lectura y estadísticas de la incubación")
    @GetMapping("/batches/{batchId}/temperature")
    public TemperatureSummaryResponse temperatureSummary(@PathVariable Long batchId) {
        return monitoringService.temperatureSummary(batchId);
    }

    @Operation(summary = "Historial de temperatura", description = "Opcionalmente filtrado entre 'start' y 'end' (ISO 8601)")
    @GetMapping("/batches/{batchId}/temperature-logs")
    public List<TemperatureLogResponse> temperatureLogs(@PathVariable Long batchId,
            @Parameter(example = "2026-05-12T08:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(example = "2026-05-12T20:00:00")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return monitoringService.temperatureLogs(batchId, start, end);
    }

    @Operation(summary = "Panel de producción", description = "Lotes por estado, lotes activos y lotes terminados hoy")
    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        return monitoringService.dashboard();
    }
}
