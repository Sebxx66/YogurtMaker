package com.sebxx66.yogurtmaker.service.simulation;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Configuración del simulador de temperatura ({@code yogurt.simulation.*}).
 *
 * @param enabled                   activa el simulador; en producción real lo reemplazaría un sensor
 * @param tick                      tiempo real entre lecturas al calentar y enfriar
 * @param incubationReadingInterval tiempo real entre lecturas durante la incubación
 * @param ambientTemperature        temperatura inicial de la leche (°C)
 */
@ConfigurationProperties("yogurt.simulation")
public record SimulationProperties(
        @DefaultValue("true") boolean enabled,
        @DefaultValue("2s") Duration tick,
        @DefaultValue("5m") Duration incubationReadingInterval,
        @DefaultValue("20") double ambientTemperature) {
}
