package com.JhoanDev87.demo.domain.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.JhoanDev87.demo.domain.model.YogurtBatch;
import com.JhoanDev87.demo.domain.service.YogurtMakingService;
import com.JhoanDev87.demo.dto.BatchDTO;
import com.JhoanDev87.demo.dto.TemperatureRecordDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batches")
@Tag(name = "Yogurt Batches", description = "Operaciones para gestionar lotes de yogurt y su ciclo de fabricación")
@RequiredArgsConstructor
public class YogurtBatchController {
    
    private final YogurtMakingService yogurtMakingService;
    
    @Operation(summary = "Iniciar lote", description = "Crea un nuevo lote de yogurt basado en una receta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Lote iniciado correctamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros de lote inválidos")
    })
    @PostMapping
    public ResponseEntity<YogurtBatch> startNewBatch(@RequestBody BatchDTO.StartBatchRequest request) {
        YogurtBatch batch = yogurtMakingService.startNewBatch(
            request.getRecipeId(), 
            request.getCustomMilkVolume(), 
            request.getCustomStarterAmount()
        );
        return new ResponseEntity<>(batch, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Iniciar calentamiento", description = "Pone el lote en la fase de calentamiento y registra el estado correspondiente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote en calentamiento"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @PostMapping("/{batchId}/heating")
    public ResponseEntity<YogurtBatch> startHeating(
            @Parameter(description = "ID del lote a calentar", required = true) @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startHeating(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @Operation(summary = "Iniciar inoculación", description = "Aplica el cultivo iniciador al lote y avanza el proceso de preparación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote en inoculación"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @PostMapping("/{batchId}/inoculating")
    public ResponseEntity<YogurtBatch> startInoculating(
            @Parameter(description = "ID del lote a inocular", required = true) @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startInoculating(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @Operation(summary = "Iniciar incubación", description = "Envía el lote a incubación a la temperatura definida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote en incubación"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @PostMapping("/{batchId}/incubation")
    public ResponseEntity<YogurtBatch> startIncubation(
            @Parameter(description = "ID del lote a incubar", required = true) @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startIncubation(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @Operation(summary = "Iniciar refrigeración", description = "Mueve el lote a refrigeración cuando ha terminado la incubación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote en refrigeración"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @PostMapping("/{batchId}/refrigeration")
    public ResponseEntity<YogurtBatch> startRefrigeration(
            @Parameter(description = "ID del lote a refrigerar", required = true) @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.startRefrigeration(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @Operation(summary = "Completar lote", description = "Marca el lote como completado una vez finalizado el proceso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote completado"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @PostMapping("/{batchId}/complete")
    public ResponseEntity<YogurtBatch> completeBatch(
            @Parameter(description = "ID del lote a completar", required = true) @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.completeBatch(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @Operation(summary = "Marcar lote fallido", description = "Registra un lote como fallido con una razón proporcionada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote marcado como fallido"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @PostMapping("/{batchId}/fail")
    public ResponseEntity<YogurtBatch> markAsFailed(
            @Parameter(description = "ID del lote a marcar como fallido", required = true) @PathVariable Long batchId, 
            @RequestBody BatchDTO.FailRequest request) {
        YogurtBatch batch = yogurtMakingService.markAsFailed(batchId, request.getReason());
        return ResponseEntity.ok(batch);
    }
    
    @Operation(summary = "Listar lotes", description = "Devuelve todos los lotes o filtra por estado si se proporciona un estado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lotes obtenidos correctamente")
    })
    @GetMapping
    public ResponseEntity<List<YogurtBatch>> getAllBatches(
            @Parameter(description = "Estado del lote para filtrar", required = false) @RequestParam(required = false) YogurtBatch.BatchStatus status) {
        if (status != null) {
            return ResponseEntity.ok(yogurtMakingService.getBatchesByStatus(status));
        }
        return ResponseEntity.ok(yogurtMakingService.getAllBatches());
    }
    
    @Operation(summary = "Obtener lote", description = "Recupera un lote de yogurt por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote encontrado"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @GetMapping("/{batchId}")
    public ResponseEntity<YogurtBatch> getBatch(
            @Parameter(description = "ID del lote a recuperar", required = true) @PathVariable Long batchId) {
        YogurtBatch batch = yogurtMakingService.getBatch(batchId);
        return ResponseEntity.ok(batch);
    }
    
    @Operation(summary = "Registrar temperatura", description = "Agrega un registro de temperatura para un lote en proceso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Temperatura registrada"),
        @ApiResponse(responseCode = "404", description = "Lote no encontrado")
    })
    @PostMapping("/{batchId}/temperature")
    public ResponseEntity<Void> recordTemperature(
            @Parameter(description = "ID del lote para el registro de temperatura", required = true) @PathVariable Long batchId, 
            @RequestBody TemperatureRecordDTO request) {
        yogurtMakingService.recordTemperature(batchId, request.getTemperature(), request.getType());
        return ResponseEntity.ok().build();
    }
}

