package com.sebxx66.yogurtmaker.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Lote de producción. Los tiempos los asigna la capa de servicio con un
 * {@link java.time.Clock} para que el proceso se pueda probar sin esperar horas.
 */
@Entity
@Table(name = "yogurt_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"recipe", "temperatureLogs"})
public class YogurtBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    private String batchCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus status;

    /** Litros de leche. */
    @Column(nullable = false)
    private Double milkVolume;

    /** Cucharadas de cultivo iniciador. */
    @Column(nullable = false)
    private Double starterAmount;

    /** Temperatura objetivo de inoculación (°C). */
    @Column(nullable = false)
    private Double targetTemperature;

    /** Horas de incubación planeadas. */
    @Column(nullable = false)
    private Integer incubationTime;

    private LocalDateTime startTime;
    private LocalDateTime incubationStartTime;
    private LocalDateTime incubationEndTime;
    private LocalDateTime refrigerationStartTime;
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TemperatureLog> temperatureLogs = new ArrayList<>();

    @Column(length = 1000)
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
