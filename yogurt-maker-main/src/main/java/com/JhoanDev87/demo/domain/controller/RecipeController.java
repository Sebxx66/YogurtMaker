package com.JhoanDev87.demo.domain.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.JhoanDev87.demo.domain.model.Recipe;
import com.JhoanDev87.demo.domain.service.RecipeService;
import com.JhoanDev87.demo.dto.RecipeDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipes", description = "API para creación, actualización y consulta de recetas de yogurt")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeService recipeService;
    
    @Operation(summary = "Crear receta", description = "Guarda una nueva receta de yogurt con ingredientes y parámetros de proceso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Receta creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de receta inválidos")
    })
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody RecipeDTO recipeDTO) {
        Recipe recipe = recipeService.createRecipe(recipeDTO);
        return new ResponseEntity<>(recipe, HttpStatus.CREATED);
    }
    
    @Operation(summary = "Actualizar receta", description = "Actualiza una receta existente por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta actualizada correctamente"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @Parameter(description = "ID de la receta a actualizar", required = true) @PathVariable Long id,
            @RequestBody RecipeDTO recipeDTO) {
        Recipe recipe = recipeService.updateRecipe(id, recipeDTO);
        return ResponseEntity.ok(recipe);
    }
    
    @Operation(summary = "Obtener receta", description = "Recupera los detalles de una receta por su identificador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta encontrada"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipe(
            @Parameter(description = "ID de la receta a buscar", required = true) @PathVariable Long id) {
        Recipe recipe = recipeService.getRecipe(id);
        return ResponseEntity.ok(recipe);
    }
    
    @Operation(summary = "Listar recetas activas", description = "Devuelve todas las recetas de yogurt activas en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de recetas activas")
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllActiveRecipes());
    }
    
    @Operation(summary = "Buscar recetas", description = "Busca recetas por palabra clave en nombre o descripción")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recetas encontradas"),
        @ApiResponse(responseCode = "400", description = "Parámetro de búsqueda inválido")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @Parameter(description = "Palabra clave utilizada para buscar recetas", required = true) @RequestParam String keyword) {
        return ResponseEntity.ok(recipeService.searchRecipes(keyword));
    }
    
    @Operation(summary = "Desactivar receta", description = "Marca una receta como inactiva para que no aparezca en listados activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta desactivada"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateRecipe(
            @Parameter(description = "ID de la receta a desactivar", required = true) @PathVariable Long id) {
        recipeService.deactivateRecipe(id);
        return ResponseEntity.ok().build();
    }
    
    @Operation(summary = "Activar receta", description = "Reactiva una receta previamente desactivada")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Receta activada"),
        @ApiResponse(responseCode = "404", description = "Receta no encontrada")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateRecipe(
            @Parameter(description = "ID de la receta a activar", required = true) @PathVariable Long id) {
        recipeService.activateRecipe(id);
        return ResponseEntity.ok().build();
    }
}

