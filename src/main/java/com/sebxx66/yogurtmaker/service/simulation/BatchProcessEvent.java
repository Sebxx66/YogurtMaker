package com.sebxx66.yogurtmaker.service.simulation;

/** Eventos que publica el servicio de lotes cuando arranca una fase con control de temperatura. */
public sealed interface BatchProcessEvent {

    Long batchId();

    record HeatingStarted(Long batchId) implements BatchProcessEvent {
    }

    record IncubationStarted(Long batchId) implements BatchProcessEvent {
    }
}
