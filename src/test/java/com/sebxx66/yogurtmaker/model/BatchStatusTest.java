package com.sebxx66.yogurtmaker.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BatchStatusTest {

    @Test
    void sigueElOrdenDelProceso() {
        assertThat(BatchStatus.PREPARING.canMoveTo(BatchStatus.HEATING)).isTrue();
        assertThat(BatchStatus.HEATING.canMoveTo(BatchStatus.COOLING)).isTrue();
        assertThat(BatchStatus.COOLING.canMoveTo(BatchStatus.INOCULATING)).isTrue();
        assertThat(BatchStatus.INOCULATING.canMoveTo(BatchStatus.INCUBATING)).isTrue();
        assertThat(BatchStatus.INCUBATING.canMoveTo(BatchStatus.REFRIGERATING)).isTrue();
        assertThat(BatchStatus.REFRIGERATING.canMoveTo(BatchStatus.COMPLETED)).isTrue();
    }

    @Test
    void noPermiteSaltarseFases() {
        assertThat(BatchStatus.PREPARING.canMoveTo(BatchStatus.INCUBATING)).isFalse();
        assertThat(BatchStatus.HEATING.canMoveTo(BatchStatus.COMPLETED)).isFalse();
        assertThat(BatchStatus.INCUBATING.canMoveTo(BatchStatus.HEATING)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = BatchStatus.class, names = {"COMPLETED", "FAILED"}, mode = EnumSource.Mode.EXCLUDE)
    void cualquierLoteEnProcesoSePuedeDescartar(BatchStatus status) {
        assertThat(status.canMoveTo(BatchStatus.FAILED)).isTrue();
        assertThat(BatchStatus.ACTIVE).contains(status);
    }

    @ParameterizedTest
    @EnumSource(value = BatchStatus.class, names = {"COMPLETED", "FAILED"})
    void unLoteTerminadoNoCambiaDeEstado(BatchStatus status) {
        for (BatchStatus next : BatchStatus.values()) {
            assertThat(status.canMoveTo(next)).isFalse();
        }
    }
}
