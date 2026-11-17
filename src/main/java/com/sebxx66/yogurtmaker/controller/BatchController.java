package com.sebxx66.yogurtmaker.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sebxx66.yogurtmaker.dto.BatchResponse;
import com.sebxx66.yogurtmaker.dto.FailBatchRequest;
import com.sebxx66.yogurtmaker.dto.StartBatchRequest;
import com.sebxx66.yogurtmaker.dto.TemperatureLogResponse;
import com.sebxx66.yogurtmaker.dto.TemperatureReadingRequest;
import com.sebxx66.yogurtmaker.model.BatchStatus;
import com.sebxx66.yogurtmaker.service.BatchService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batches")
@Tag(name = "Lotes", description = "Ciclo de fabricación: PREPARING → HEATING → COOLING → INOCULATING → "
        + "INCUBATING → REFRIGERATING → COMPLETED")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @Operation(summary = "Iniciar lote", description = "Crea un lote en estado PREPARING a partir de una receta activa")
    @ApiResponse(responseCode = "201", description = "Lote creado")
    @ApiResponse(responseCode = "404", description = "La receta no existe")
    @ApiResponse(responseCode = "422", description = "Receta desactivada o tiempo de incubación fuera de rango")
    @PostMapping
    public ResponseEntity<BatchResponse> start(@Valid @RequestBody StartBatchRequest request) {
        BatchResponse created = batchService.start(request);
        return ResponseEntity.created(URI.create("/api/batches/" + created.id())).body(created);
    }

    @Operation(summary = "Listar lotes", description = "Del más reciente al más antiguo; se puede filtrar por estado")
    @GetMapping
    public List<BatchResponse> list(@RequestParam(required = false) BatchStatus status) {
        return batchService.list(status);
    }

    @Operation(summary = "Consultar lote")
    @ApiResponse(responseCode = "404", description = "El lote no existe")
    @GetMapping("/{batchId}")
    public BatchResponse get(@PathVariable Long batchId) {
        return batchService.get(batchId);
    }

    @Operation(summary = "Calentar", description = "PREPARING → HEATING. Con el simulador activo, el lote se calienta, "
            + "mantiene la temperatura y pasa solo a COOLING")
    @ApiResponse(responseCode = "409", description = "El lote no está en PREPARING")
    @PostMapping("/{batchId}/heating")
    public BatchResponse startHeating(@PathVariable Long batchId) {
        return batchService.startHeating(batchId);
    }

    @Operation(summary = "Enfriar", description = "HEATING → COOLING. Para avanzar manualmente si no se usa el simulador")
    @ApiResponse(responseCode = "409", description = "El lote no está en HEATING")
    @PostMapping("/{batchId}/cooling")
    public BatchResponse startCooling(@PathVariable Long batchId) {
        return batchService.startCooling(batchId);
    }

    @Operation(summary = "Inocular", description = "COOLING → INOCULATING")
    @ApiResponse(responseCode = "409", description = "El lote no está en COOLING")
    @PostMapping("/{batchId}/inoculating")
    public BatchResponse startInoculating(@PathVariable Long batchId) {
        return batchService.startInoculating(batchId);
    }

    @Operation(summary = "Incubar", description = "INOCULATING → INCUBATING. Calcula la hora de fin de la incubación")
    @ApiResponse(responseCode = "409", description = "El lote no está en INOCULATING")
    @PostMapping("/{batchId}/incubation")
    public BatchResponse startIncubation(@PathVariable Long batchId) {
        return batchService.startIncubation(batchId);
    }

    @Operation(summary = "Refrigerar", description = "INCUBATING → REFRIGERATING, solo cuando terminó la incubación")
    @ApiResponse(responseCode = "409", description = "La incubación no ha terminado o el lote no está en INCUBATING")
    @PostMapping("/{batchId}/refrigeration")
    public BatchResponse startRefrigeration(@PathVariable Long batchId) {
        return batchService.startRefrigeration(batchId);
    }

    @Operation(summary = "Completar", description = "REFRIGERATING → COMPLETED, cuando pasó el tiempo de refrigeración")
    @ApiResponse(responseCode = "409", description = "Falta tiempo de refrigeración o el lote no está en REFRIGERATING")
    @PostMapping("/{batchId}/complete")
    public BatchResponse complete(@PathVariable Long batchId) {
        return batchService.complete(batchId);
    }

    @Operation(summary = "Descartar lote", description = "Cualquier estado en proceso → FAILED")
    @ApiResponse(responseCode = "409", description = "El lote ya estaba terminado")
    @PostMapping("/{batchId}/fail")
    public BatchResponse markAsFailed(@PathVariable Long batchId, @Valid @RequestBody FailBatchRequest request) {
        return batchService.markAsFailed(batchId, request.reason());
    }

    @Operation(summary = "Registrar temperatura", description = "Agrega una lectura manual a un lote en proceso")
    @ApiResponse(responseCode = "201", description = "Lectura registrada")
    @ApiResponse(responseCode = "409", description = "El lote ya terminó")
    @PostMapping("/{batchId}/temperature")
    public ResponseEntity<TemperatureLogResponse> recordTemperature(@PathVariable Long batchId,
            @Valid @RequestBody TemperatureReadingRequest request) {
        TemperatureLogResponse reading = batchService.recordTemperature(batchId, request.temperature(),
                request.type(), request.notes());
        return ResponseEntity.status(201).body(reading);
    }
}
