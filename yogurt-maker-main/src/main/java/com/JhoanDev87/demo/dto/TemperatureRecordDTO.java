package com.JhoanDev87.demo.dto;

import com.JhoanDev87.demo.domain.model.TemperatureLog;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;

@Data
@Schema(description = "Registro de temperatura para un lote de yogurt")
public class TemperatureRecordDTO {
    @Schema(description = "Temperatura registrada en grados Celsius", example = "42.3")
    private Double temperature;

    @Schema(description = "Tipo de registro de temperatura")
    private TemperatureLog.LogType type;
}


