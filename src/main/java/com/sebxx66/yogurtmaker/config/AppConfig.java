package com.sebxx66.yogurtmaker.config;

import java.time.Clock;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.sebxx66.yogurtmaker.service.simulation.SimulationProperties;

@Configuration
@EnableAsync
@EnableConfigurationProperties(SimulationProperties.class)
public class AppConfig {

    /** Reloj del sistema. Las pruebas lo reemplazan para avanzar el tiempo sin esperar. */
    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
