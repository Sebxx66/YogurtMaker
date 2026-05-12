package com.JhoanDev87.demo.dto;

import java.util.List;

import com.JhoanDev87.demo.domain.model.Recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para crear o actualizar una receta de yogurt")
public class RecipeDTO {
    @Schema(description = "Nombre de la receta", example = "Yogurt Natural")
    private String name;

    @Schema(description = "Descripción detallada de la receta", example = "Yogurt cremoso con cultivo natural")
    private String description;

    @Schema(description = "Volumen de leche por defecto en litros", example = "1.5")
    private Double defaultMilkVolume;

    @Schema(description = "Cantidad de iniciador por defecto en cucharadas", example = "2.0")
    private Double defaultStarterAmount;

    @Schema(description = "Temperatura de calentamiento en grados Celsius", example = "85")
    private Double heatingTemperature;

    @Schema(description = "Duración del calentamiento en minutos", example = "15")
    private Integer heatingDuration;

    @Schema(description = "Temperatura de inoculación en grados Celsius", example = "45")
    private Double inoculationTemperature;

    @Schema(description = "Temperatura de incubación en grados Celsius", example = "42")
    private Double incubationTemperature;

    @Schema(description = "Tiempo mínimo de incubación en horas", example = "6")
    private Integer minIncubationTime;

    @Schema(description = "Tiempo máximo de incubación en horas", example = "12")
    private Integer maxIncubationTime;

    @Schema(description = "Tiempo de refrigeración recomendado en horas", example = "4")
    private Integer refrigerationTime;

    @Schema(description = "Nivel de dificultad de la receta")
    private Recipe.DifficultyLevel difficulty;

    @Schema(description = "Consejos adicionales para la preparación", example = "Usar leche entera para mayor cremosidad")
    private String tips;

    @Schema(description = "Lista de ingredientes requeridos para la receta")
    private List<IngredientDTO> ingredients;
}
