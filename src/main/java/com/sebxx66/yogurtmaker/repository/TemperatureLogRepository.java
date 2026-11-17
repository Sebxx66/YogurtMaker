package com.sebxx66.yogurtmaker.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sebxx66.yogurtmaker.model.TemperatureLog;

public interface TemperatureLogRepository extends JpaRepository<TemperatureLog, Long> {

    List<TemperatureLog> findByBatchIdOrderByRecordedAtAsc(Long batchId);

    List<TemperatureLog> findByBatchIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long batchId, LocalDateTime start, LocalDateTime end);

    Optional<TemperatureLog> findFirstByBatchIdOrderByRecordedAtDescIdDesc(Long batchId);

    @Query("""
            SELECT MIN(t.temperature) AS minimum, MAX(t.temperature) AS maximum, AVG(t.temperature) AS average,
                   COUNT(t) AS readings
            FROM TemperatureLog t
            WHERE t.batch.id = :batchId AND t.type = :type
            """)
    TemperatureStats statsByBatchAndType(@Param("batchId") Long batchId, @Param("type") TemperatureLog.LogType type);

    interface TemperatureStats {
        Double getMinimum();

        Double getMaximum();

        Double getAverage();

        long getReadings();
    }
}
