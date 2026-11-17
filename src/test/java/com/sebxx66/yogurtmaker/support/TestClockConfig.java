package com.sebxx66.yogurtmaker.support;

import java.time.Instant;
import java.time.ZoneId;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestClockConfig {

    @Bean
    @Primary
    MutableClock testClock() {
        return new MutableClock(Instant.parse("2026-05-12T13:00:00Z"), ZoneId.of("America/Bogota"));
    }
}
