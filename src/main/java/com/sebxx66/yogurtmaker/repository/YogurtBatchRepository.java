package com.sebxx66.yogurtmaker.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sebxx66.yogurtmaker.model.BatchStatus;
import com.sebxx66.yogurtmaker.model.YogurtBatch;

public interface YogurtBatchRepository extends JpaRepository<YogurtBatch, Long> {

    List<YogurtBatch> findByStatusOrderByCreatedAtDesc(BatchStatus status);

    List<YogurtBatch> findByStatusInOrderByCreatedAtDesc(Collection<BatchStatus> statuses);

    List<YogurtBatch> findAllByOrderByCreatedAtDesc();

    long countByStatus(BatchStatus status);

    long countByStatusAndCompletedAtGreaterThanEqual(BatchStatus status, LocalDateTime since);

    @Query("SELECT b.status AS status, COUNT(b) AS total FROM YogurtBatch b GROUP BY b.status")
    List<StatusCount> countGroupedByStatus();

    interface StatusCount {
        BatchStatus getStatus();

        long getTotal();
    }
}
