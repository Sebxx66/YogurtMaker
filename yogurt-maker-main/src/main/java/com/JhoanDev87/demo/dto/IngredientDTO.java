package com.JhoanDev87.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ingrediente usado en una receta de yogurt")
public class IngredientDTO {
    @Schema(description = "Nombre del ingrediente", example = "Leche")
    private String name;

    @Schema(description = "Cantidad del ingrediente", example = "1.0")
    private Double quantity;

    @Schema(description = "Unidad de medida del ingrediente", example = "L")
    private String unit;

    @Schema(description = "Notas opcionales sobre el ingrediente", example = "Usar leche entera")
    private String notes;

    @Schema(description = "Indica si el ingrediente es opcional")
    private Boolean optional;
}

