package com.sebxx66.yogurtmaker.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Receta de yogurt con los parámetros de cada fase del proceso.
 */
@Entity
@Table(name = "recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "ingredients")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Ingredient> ingredients = new ArrayList<>();

    /** Litros de leche por defecto. */
    @Column(nullable = false)
    private Double defaultMilkVolume;

    /** Cucharadas de cultivo iniciador por defecto. */
    @Column(nullable = false)
    private Double defaultStarterAmount;

    /** Temperatura de calentamiento (°C). */
    @Column(nullable = false)
    private Double heatingTemperature;

    /** Minutos que se mantiene la temperatura de calentamiento. */
    @Column(nullable = false)
    private Integer heatingDuration;

    /** Temperatura a la que se agrega el cultivo (°C). */
    @Column(nullable = false)
    private Double inoculationTemperature;

    /** Temperatura de incubación (°C). */
    @Column(nullable = false)
    private Double incubationTemperature;

    /** Horas mínimas de incubación. */
    @Column(nullable = false)
    private Integer minIncubationTime;

    /** Horas máximas de incubación. */
    @Column(nullable = false)
    private Integer maxIncubationTime;

    /** Horas de refrigeración antes de dar el lote por terminado. */
    @Column(nullable = false)
    private Integer refrigerationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DifficultyLevel difficulty;

    @Column(length = 1000)
    private String tips;

    @Column(nullable = false)
    private Boolean active;

    public void replaceIngredients(List<Ingredient> newIngredients) {
        ingredients.clear();
        newIngredients.forEach(this::addIngredient);
    }

    public void addIngredient(Ingredient ingredient) {
        ingredient.setRecipe(this);
        ingredients.add(ingredient);
    }

    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
