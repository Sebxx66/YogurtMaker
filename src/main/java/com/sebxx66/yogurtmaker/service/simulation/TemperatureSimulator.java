package com.sebxx66.yogurtmaker.service.simulation;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sebxx66.yogurtmaker.dto.BatchResponse;
import com.sebxx66.yogurtmaker.dto.RecipeResponse;
import com.sebxx66.yogurtmaker.model.BatchStatus;
import com.sebxx66.yogurtmaker.model.TemperatureLog.LogType;
import com.sebxx66.yogurtmaker.service.BatchService;
import com.sebxx66.yogurtmaker.service.RecipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Simula los sensores de temperatura de un lote. Escucha los eventos del servicio
 * de lotes después de que la transacción se confirma y trabaja en segundo plano.
 *
 * <p>Cada lectura y cada cambio de estado pasa por {@link BatchService}, así el
 * simulador respeta las mismas reglas que un usuario. Si alguien cambia el lote
 * (por ejemplo, lo descarta), el simulador lo detecta y se detiene.
 */
@Component
@ConditionalOnProperty(prefix = "yogurt.simulation", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class TemperatureSimulator {

    private static final double TOLERANCE = 0.5;

    private final BatchService batchService;
    private final RecipeService recipeService;
    private final SimulationProperties properties;
    private final Clock clock;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onHeatingStarted(BatchProcessEvent.HeatingStarted event) {
        Long batchId = event.batchId();
        try {
            RecipeResponse recipe = recipeOf(batchId);
            double temperature = properties.ambientTemperature();

            while (temperature < recipe.heatingTemperature() - TOLERANCE) {
                if (!pause(properties.tick()) || !isIn(batchId, BatchStatus.HEATING)) {
                    return;
                }
                temperature += (recipe.heatingTemperature() - temperature) * 0.25 + noise(0.3);
                batchService.recordTemperature(batchId, temperature, LogType.HEATING, null);
            }

            // Mantiene la temperatura: una lectura por cada minuto de la receta.
            for (int minute = 0; minute < recipe.heatingDuration(); minute++) {
                if (!pause(properties.tick()) || !isIn(batchId, BatchStatus.HEATING)) {
                    return;
                }
                batchService.recordTemperature(batchId, recipe.heatingTemperature() + noise(1.0), LogType.HEATING, null);
            }

            batchService.startCooling(batchId);
            temperature = recipe.heatingTemperature();
            while (temperature > recipe.inoculationTemperature() + TOLERANCE) {
                if (!pause(properties.tick()) || !isIn(batchId, BatchStatus.COOLING)) {
                    return;
                }
                temperature -= (temperature - recipe.inoculationTemperature()) * 0.2 + noise(0.3);
                batchService.recordTemperature(batchId, temperature, LogType.COOLING, null);
            }
            log.info("Lote {} listo para inocular a {} °C", batchId, recipe.inoculationTemperature());
        } catch (RuntimeException ex) {
            log.warn("Simulación de calentamiento detenida para el lote {}: {}", batchId, ex.getMessage());
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onIncubationStarted(BatchProcessEvent.IncubationStarted event) {
        Long batchId = event.batchId();
        try {
            BatchResponse batch = batchService.get(batchId);
            RecipeResponse recipe = recipeService.get(batch.recipeId());
            while (LocalDateTime.now(clock).isBefore(batch.incubationEndTime())) {
                if (!pause(properties.incubationReadingInterval()) || !isIn(batchId, BatchStatus.INCUBATING)) {
                    return;
                }
                batchService.recordTemperature(batchId, recipe.incubationTemperature() + noise(0.4),
                        LogType.INCUBATION, null);
            }
            log.info("Lote {} terminó la incubación; ya se puede refrigerar", batchId);
        } catch (RuntimeException ex) {
            log.warn("Simulación de incubación detenida para el lote {}: {}", batchId, ex.getMessage());
        }
    }

    private RecipeResponse recipeOf(Long batchId) {
        return recipeService.get(batchService.get(batchId).recipeId());
    }

    private boolean isIn(Long batchId, BatchStatus expected) {
        return batchService.statusOf(batchId) == expected;
    }

    private static double noise(double amplitude) {
        return ThreadLocalRandom.current().nextDouble(-amplitude, amplitude);
    }

    /** Espera el tiempo indicado; devuelve false si el hilo fue interrumpido (por ejemplo, al apagar la app). */
    private static boolean pause(Duration duration) {
        try {
            Thread.sleep(duration);
            return true;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
