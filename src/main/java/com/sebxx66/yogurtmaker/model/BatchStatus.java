package com.sebxx66.yogurtmaker.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * Fases del ciclo de vida de un lote. Cada fase sabe a cuál puede avanzar,
 * así las reglas del proceso viven en un solo lugar.
 */
public enum BatchStatus {
    PREPARING,
    HEATING,
    COOLING,
    INOCULATING,
    INCUBATING,
    REFRIGERATING,
    COMPLETED,
    FAILED;

    /** Estados en los que el lote sigue en producción. */
    public static final Set<BatchStatus> ACTIVE =
            EnumSet.of(PREPARING, HEATING, COOLING, INOCULATING, INCUBATING, REFRIGERATING);

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED;
    }

    public boolean canMoveTo(BatchStatus next) {
        if (next == FAILED) {
            return !isFinal();
        }
        return switch (this) {
            case PREPARING -> next == HEATING;
            case HEATING -> next == COOLING;
            case COOLING -> next == INOCULATING;
            case INOCULATING -> next == INCUBATING;
            case INCUBATING -> next == REFRIGERATING;
            case REFRIGERATING -> next == COMPLETED;
            case COMPLETED, FAILED -> false;
        };
    }
}
