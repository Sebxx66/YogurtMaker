package com.JhoanDev87.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Contiene objetos para crear y actualizar lotes de yogurt")
public class BatchDTO {
    
    @Data
    @Schema(description = "Solicitud para iniciar un nuevo lote de yogurt")
    public static class StartBatchRequest {
        @Schema(description = "ID de la receta que se utilizará", example = "1")
        private Long recipeId;

        @Schema(description = "Volumen de leche personalizado en litros", example = "1.25")
        private Double customMilkVolume;

        @Schema(description = "Cantidad de iniciador personalizado en cucharadas", example = "2.0")
        private Double customStarterAmount;
    }
    
    @Data
    @Schema(description = "Solicitud para marcar un lote como fallido")
    public static class FailRequest {
        @Schema(description = "Razón por la que el lote falló", example = "Temperatura fuera de rango")
        private String reason;
    }
}

