package com.sebxx66.yogurtmaker.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sebxx66.yogurtmaker.dto.IngredientRequest;
import com.sebxx66.yogurtmaker.dto.RecipeRequest;
import com.sebxx66.yogurtmaker.dto.RecipeResponse;
import com.sebxx66.yogurtmaker.exception.BusinessException;
import com.sebxx66.yogurtmaker.exception.DuplicateResourceException;
import com.sebxx66.yogurtmaker.exception.ResourceNotFoundException;
import com.sebxx66.yogurtmaker.model.Ingredient;
import com.sebxx66.yogurtmaker.model.Recipe;
import com.sebxx66.yogurtmaker.repository.RecipeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RecipeService {

    private final RecipeRepository recipeRepository;

    @Transactional
    public RecipeResponse create(RecipeRequest request) {
        validateProcessParameters(request);
        if (recipeRepository.existsByNameIgnoreCase(request.name().trim())) {
            throw new DuplicateResourceException("Ya existe una receta llamada '" + request.name().trim() + "'");
        }
        Recipe recipe = Recipe.builder().active(true).build();
        apply(request, recipe);
        Recipe saved = recipeRepository.save(recipe);
        log.info("Receta creada: {} (id {})", saved.getName(), saved.getId());
        return RecipeResponse.from(saved);
    }

    @Transactional
    public RecipeResponse update(Long id, RecipeRequest request) {
        validateProcessParameters(request);
        Recipe recipe = findEntity(id);
        if (recipeRepository.existsByNameIgnoreCaseAndIdNot(request.name().trim(), id)) {
            throw new DuplicateResourceException("Ya existe otra receta llamada '" + request.name().trim() + "'");
        }
        apply(request, recipe);
        recipeRepository.flush();
        log.info("Receta actualizada: {} (id {})", recipe.getName(), id);
        return RecipeResponse.from(recipe);
    }

    public RecipeResponse get(Long id) {
        return RecipeResponse.from(findEntity(id));
    }

    public List<RecipeResponse> listActive() {
        return recipeRepository.findByActiveTrueOrderByNameAsc().stream().map(RecipeResponse::from).toList();
    }

    public List<RecipeResponse> search(String keyword) {
        return recipeRepository.searchByKeyword(keyword.trim()).stream().map(RecipeResponse::from).toList();
    }

    @Transactional
    public RecipeResponse setActive(Long id, boolean active) {
        Recipe recipe = findEntity(id);
        recipe.setActive(active);
        log.info("Receta {} {}", recipe.getName(), active ? "activada" : "desactivada");
        return RecipeResponse.from(recipe);
    }

    Recipe findEntity(Long id) {
        return recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("La receta", id));
    }

    /** Reglas que relacionan varios campos y que las anotaciones no cubren. */
    private void validateProcessParameters(RecipeRequest request) {
        if (request.minIncubationTime() > request.maxIncubationTime()) {
            throw new BusinessException("El tiempo mínimo de incubación no puede ser mayor que el máximo");
        }
        if (request.inoculationTemperature() >= request.heatingTemperature()) {
            throw new BusinessException("La temperatura de inoculación debe ser menor que la de calentamiento");
        }
    }

    private void apply(RecipeRequest request, Recipe recipe) {
        recipe.setName(request.name().trim());
        recipe.setDescription(request.description());
        recipe.setDefaultMilkVolume(request.defaultMilkVolume());
        recipe.setDefaultStarterAmount(request.defaultStarterAmount());
        recipe.setHeatingTemperature(request.heatingTemperature());
        recipe.setHeatingDuration(request.heatingDuration());
        recipe.setInoculationTemperature(request.inoculationTemperature());
        recipe.setIncubationTemperature(request.incubationTemperature());
        recipe.setMinIncubationTime(request.minIncubationTime());
        recipe.setMaxIncubationTime(request.maxIncubationTime());
        recipe.setRefrigerationTime(request.refrigerationTime());
        recipe.setDifficulty(request.difficulty());
        recipe.setTips(request.tips());
        List<IngredientRequest> ingredients = request.ingredients() == null ? List.of() : request.ingredients();
        recipe.replaceIngredients(ingredients.stream().map(this::toIngredient).toList());
    }

    private Ingredient toIngredient(IngredientRequest request) {
        return Ingredient.builder()
                .name(request.name().trim())
                .quantity(request.quantity())
                .unit(request.unit())
                .notes(request.notes())
                .optional(Boolean.TRUE.equals(request.optional()))
                .build();
    }
}
