package com.sebxx66.yogurtmaker.support;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

/** Reloj que las pruebas pueden adelantar para simular horas de incubación o refrigeración. */
public class MutableClock extends Clock {

    private Instant instant;
    private final ZoneId zone;

    public MutableClock(Instant start, ZoneId zone) {
        this.instant = start;
        this.zone = zone;
    }

    public void advance(Duration duration) {
        instant = instant.plus(duration);
    }

    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId newZone) {
        return new MutableClock(instant, newZone);
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
