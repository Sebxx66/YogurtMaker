package com.sebxx66.yogurtmaker.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sebxx66.yogurtmaker.dto.BatchResponse;
import com.sebxx66.yogurtmaker.dto.DashboardResponse;
import com.sebxx66.yogurtmaker.dto.TemperatureLogResponse;
import com.sebxx66.yogurtmaker.dto.TemperatureSummaryResponse;
import com.sebxx66.yogurtmaker.exception.BusinessException;
import com.sebxx66.yogurtmaker.model.BatchStatus;
import com.sebxx66.yogurtmaker.model.TemperatureLog;
import com.sebxx66.yogurtmaker.model.TemperatureLog.LogType;
import com.sebxx66.yogurtmaker.repository.TemperatureLogRepository;
import com.sebxx66.yogurtmaker.repository.TemperatureLogRepository.TemperatureStats;
import com.sebxx66.yogurtmaker.repository.YogurtBatchRepository;

import lombok.RequiredArgsConstructor;

/** Consultas de solo lectura sobre el estado de la producción. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonitoringService {

    private final YogurtBatchRepository batchRepository;
    private final TemperatureLogRepository temperatureLogRepository;
    private final BatchService batchService;
    private final Clock clock;

    public List<BatchResponse> activeBatches() {
        return batchRepository.findByStatusInOrderByCreatedAtDesc(BatchStatus.ACTIVE).stream()
                .map(BatchResponse::from)
                .toList();
    }

    public TemperatureSummaryResponse temperatureSummary(Long batchId) {
        batchService.findEntity(batchId);
        Double current = temperatureLogRepository.findFirstByBatchIdOrderByRecordedAtDescIdDesc(batchId)
                .map(TemperatureLog::getTemperature)
                .orElse(null);
        TemperatureStats stats = temperatureLogRepository.statsByBatchAndType(batchId, LogType.INCUBATION);
        Double average = stats.getAverage() == null ? null : Math.round(stats.getAverage() * 10) / 10.0;
        return new TemperatureSummaryResponse(current, stats.getMinimum(), stats.getMaximum(), average,
                stats.getReadings());
    }

    public List<TemperatureLogResponse> temperatureLogs(Long batchId, LocalDateTime start, LocalDateTime end) {
        batchService.findEntity(batchId);
        if ((start == null) != (end == null)) {
            throw new BusinessException("Para filtrar por fechas envía 'start' y 'end' juntos");
        }
        List<TemperatureLog> logs;
        if (start != null) {
            if (start.isAfter(end)) {
                throw new BusinessException("'start' debe ser anterior a 'end'");
            }
            logs = temperatureLogRepository.findByBatchIdAndRecordedAtBetweenOrderByRecordedAtAsc(batchId, start, end);
        } else {
            logs = temperatureLogRepository.findByBatchIdOrderByRecordedAtAsc(batchId);
        }
        return logs.stream().map(TemperatureLogResponse::from).toList();
    }

    public DashboardResponse dashboard() {
        Map<BatchStatus, Long> counts = new EnumMap<>(BatchStatus.class);
        for (BatchStatus status : BatchStatus.values()) {
            counts.put(status, 0L);
        }
        batchRepository.countGroupedByStatus().forEach(row -> counts.put(row.getStatus(), row.getTotal()));

        long active = BatchStatus.ACTIVE.stream().mapToLong(counts::get).sum();
        LocalDateTime startOfDay = LocalDate.now(clock).atStartOfDay();
        long completedToday = batchRepository.countByStatusAndCompletedAtGreaterThanEqual(
                BatchStatus.COMPLETED, startOfDay);
        return new DashboardResponse(counts, active, completedToday);
    }
}
