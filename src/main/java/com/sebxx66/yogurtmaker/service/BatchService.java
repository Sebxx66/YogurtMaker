package com.sebxx66.yogurtmaker.service;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sebxx66.yogurtmaker.dto.BatchResponse;
import com.sebxx66.yogurtmaker.dto.StartBatchRequest;
import com.sebxx66.yogurtmaker.dto.TemperatureLogResponse;
import com.sebxx66.yogurtmaker.exception.BusinessException;
import com.sebxx66.yogurtmaker.exception.InvalidStateException;
import com.sebxx66.yogurtmaker.exception.ResourceNotFoundException;
import com.sebxx66.yogurtmaker.model.BatchStatus;
import com.sebxx66.yogurtmaker.model.Recipe;
import com.sebxx66.yogurtmaker.model.TemperatureLog;
import com.sebxx66.yogurtmaker.model.TemperatureLog.LogType;
import com.sebxx66.yogurtmaker.model.YogurtBatch;
import com.sebxx66.yogurtmaker.repository.TemperatureLogRepository;
import com.sebxx66.yogurtmaker.repository.YogurtBatchRepository;
import com.sebxx66.yogurtmaker.service.simulation.BatchProcessEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Ciclo de vida de los lotes: PREPARING → HEATING → COOLING → INOCULATING →
 * INCUBATING → REFRIGERATING → COMPLETED. Cualquier lote en proceso puede pasar a FAILED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BatchService {

    private static final DateTimeFormatter CODE_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final YogurtBatchRepository batchRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final RecipeService recipeService;
    private final ApplicationEventPublisher events;
    private final Clock clock;

    @Transactional
    public BatchResponse start(StartBatchRequest request) {
        Recipe recipe = recipeService.findEntity(request.recipeId());
        if (!Boolean.TRUE.equals(recipe.getActive())) {
            throw new BusinessException("La receta '" + recipe.getName() + "' está desactivada");
        }

        int incubationHours = request.customIncubationHours() != null
                ? request.customIncubationHours()
                : recipe.getMinIncubationTime();
        if (incubationHours < recipe.getMinIncubationTime() || incubationHours > recipe.getMaxIncubationTime()) {
            throw new BusinessException("La incubación de esta receta debe durar entre "
                    + recipe.getMinIncubationTime() + " y " + recipe.getMaxIncubationTime() + " horas");
        }

        LocalDateTime now = now();
        YogurtBatch batch = YogurtBatch.builder()
                .batchCode(newBatchCode(now))
                .recipe(recipe)
                .status(BatchStatus.PREPARING)
                .milkVolume(orDefault(request.customMilkVolume(), recipe.getDefaultMilkVolume()))
                .starterAmount(orDefault(request.customStarterAmount(), recipe.getDefaultStarterAmount()))
                .targetTemperature(recipe.getInoculationTemperature())
                .incubationTime(incubationHours)
                .startTime(now)
                .createdAt(now)
                .build();

        YogurtBatch saved = batchRepository.save(batch);
        log.info("Lote {} iniciado con la receta {}", saved.getBatchCode(), recipe.getName());
        return BatchResponse.from(saved);
    }

    @Transactional
    public BatchResponse startHeating(Long batchId) {
        YogurtBatch batch = moveTo(batchId, BatchStatus.HEATING);
        events.publishEvent(new BatchProcessEvent.HeatingStarted(batch.getId()));
        return BatchResponse.from(batch);
    }

    /** Termina el calentamiento y empieza a enfriar hasta la temperatura de inoculación. */
    @Transactional
    public BatchResponse startCooling(Long batchId) {
        return BatchResponse.from(moveTo(batchId, BatchStatus.COOLING));
    }

    @Transactional
    public BatchResponse startInoculating(Long batchId) {
        return BatchResponse.from(moveTo(batchId, BatchStatus.INOCULATING));
    }

    @Transactional
    public BatchResponse startIncubation(Long batchId) {
        YogurtBatch batch = moveTo(batchId, BatchStatus.INCUBATING);
        LocalDateTime now = now();
        batch.setIncubationStartTime(now);
        batch.setIncubationEndTime(now.plusHours(batch.getIncubationTime()));
        events.publishEvent(new BatchProcessEvent.IncubationStarted(batch.getId()));
        return BatchResponse.from(batch);
    }

    @Transactional
    public BatchResponse startRefrigeration(Long batchId) {
        YogurtBatch batch = findEntity(batchId);
        if (batch.getStatus() == BatchStatus.INCUBATING && now().isBefore(batch.getIncubationEndTime())) {
            throw new InvalidStateException("La incubación del lote " + batch.getBatchCode()
                    + " termina a las " + batch.getIncubationEndTime());
        }
        transition(batch, BatchStatus.REFRIGERATING);
        batch.setRefrigerationStartTime(now());
        return BatchResponse.from(batch);
    }

    @Transactional
    public BatchResponse complete(Long batchId) {
        YogurtBatch batch = findEntity(batchId);
        if (batch.getStatus() == BatchStatus.REFRIGERATING) {
            LocalDateTime readyAt = batch.getRefrigerationStartTime()
                    .plusHours(batch.getRecipe().getRefrigerationTime());
            if (now().isBefore(readyAt)) {
                throw new InvalidStateException("El lote " + batch.getBatchCode()
                        + " debe seguir refrigerándose hasta las " + readyAt);
            }
        }
        transition(batch, BatchStatus.COMPLETED);
        batch.setCompletedAt(now());
        return BatchResponse.from(batch);
    }

    @Transactional
    public BatchResponse markAsFailed(Long batchId, String reason) {
        YogurtBatch batch = moveTo(batchId, BatchStatus.FAILED);
        batch.setNotes("Descartado: " + reason.trim());
        log.warn("Lote {} descartado: {}", batch.getBatchCode(), reason);
        return BatchResponse.from(batch);
    }

    @Transactional
    public TemperatureLogResponse recordTemperature(Long batchId, double temperature, LogType type, String notes) {
        YogurtBatch batch = findEntity(batchId);
        if (batch.getStatus().isFinal()) {
            throw new InvalidStateException("El lote " + batch.getBatchCode() + " ya terminó; no admite lecturas");
        }
        TemperatureLog reading = TemperatureLog.builder()
                .batch(batch)
                .temperature(Math.round(temperature * 10) / 10.0)
                .type(type == null ? LogType.MANUAL : type)
                .recordedAt(now())
                .notes(notes)
                .build();
        return TemperatureLogResponse.from(temperatureLogRepository.save(reading));
    }

    public BatchResponse get(Long batchId) {
        return BatchResponse.from(findEntity(batchId));
    }

    public BatchStatus statusOf(Long batchId) {
        return findEntity(batchId).getStatus();
    }

    public List<BatchResponse> list(BatchStatus status) {
        List<YogurtBatch> batches = status == null
                ? batchRepository.findAllByOrderByCreatedAtDesc()
                : batchRepository.findByStatusOrderByCreatedAtDesc(status);
        return batches.stream().map(BatchResponse::from).toList();
    }

    YogurtBatch findEntity(Long batchId) {
        return batchRepository.findById(batchId).orElseThrow(() -> new ResourceNotFoundException("El lote", batchId));
    }

    private YogurtBatch moveTo(Long batchId, BatchStatus next) {
        YogurtBatch batch = findEntity(batchId);
        transition(batch, next);
        return batch;
    }

    private void transition(YogurtBatch batch, BatchStatus next) {
        BatchStatus current = batch.getStatus();
        if (!current.canMoveTo(next)) {
            throw new InvalidStateException("El lote " + batch.getBatchCode() + " está en " + current
                    + " y no puede pasar a " + next);
        }
        batch.setStatus(next);
        batch.setUpdatedAt(now());
        batchRepository.flush();
        log.info("Lote {}: {} → {}", batch.getBatchCode(), current, next);
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    private static String newBatchCode(LocalDateTime now) {
        StringBuilder suffix = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            suffix.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return "YB-" + now.format(CODE_DATE) + "-" + suffix;
    }

    private static Double orDefault(Double value, Double fallback) {
        return value != null ? value : fallback;
    }
}
