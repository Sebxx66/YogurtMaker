package com.sebxx66.yogurtmaker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Motivo por el que se descarta un lote")
public record FailBatchRequest(
        @Schema(example = "Temperatura fuera de rango") @NotBlank(message = "El motivo es obligatorio") @Size(max = 500)
        String reason) {
}
