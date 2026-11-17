package com.sebxx66.yogurtmaker.controller;

import java.net.URI;
import java.util.List;

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

import com.sebxx66.yogurtmaker.dto.RecipeRequest;
import com.sebxx66.yogurtmaker.dto.RecipeResponse;
import com.sebxx66.yogurtmaker.service.RecipeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recetas", description = "Creación, edición y consulta de recetas")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "Crear receta")
    @ApiResponse(responseCode = "201", description = "Receta creada")
    @ApiResponse(responseCode = "400", description = "Campos inválidos")
    @ApiResponse(responseCode = "409", description = "Ya existe una receta con ese nombre")
    @ApiResponse(responseCode = "422", description = "Parámetros del proceso incoherentes")
    @PostMapping
    public ResponseEntity<RecipeResponse> create(@Valid @RequestBody RecipeRequest request) {
        RecipeResponse created = recipeService.create(request);
        return ResponseEntity.created(URI.create("/api/recipes/" + created.id())).body(created);
    }

    @Operation(summary = "Actualizar receta")
    @ApiResponse(responseCode = "200", description = "Receta actualizada")
    @ApiResponse(responseCode = "404", description = "La receta no existe")
    @PutMapping("/{id}")
    public RecipeResponse update(@PathVariable Long id, @Valid @RequestBody RecipeRequest request) {
        return recipeService.update(id, request);
    }

    @Operation(summary = "Consultar receta")
    @ApiResponse(responseCode = "200", description = "Receta encontrada")
    @ApiResponse(responseCode = "404", description = "La receta no existe")
    @GetMapping("/{id}")
    public RecipeResponse get(@PathVariable Long id) {
        return recipeService.get(id);
    }

    @Operation(summary = "Listar recetas activas")
    @GetMapping
    public List<RecipeResponse> listActive() {
        return recipeService.listActive();
    }

    @Operation(summary = "Buscar recetas", description = "Busca por nombre o descripción, sin distinguir mayúsculas")
    @GetMapping("/search")
    public List<RecipeResponse> search(@RequestParam @NotBlank String keyword) {
        return recipeService.search(keyword);
    }

    @Operation(summary = "Desactivar receta", description = "Deja de aparecer en el listado y no se puede usar en lotes nuevos")
    @PatchMapping("/{id}/deactivate")
    public RecipeResponse deactivate(@PathVariable Long id) {
        return recipeService.setActive(id, false);
    }

    @Operation(summary = "Activar receta")
    @PatchMapping("/{id}/activate")
    public RecipeResponse activate(@PathVariable Long id) {
        return recipeService.setActive(id, true);
    }
}
