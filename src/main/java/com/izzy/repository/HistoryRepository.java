package com.izzy.repository;

import com.izzy.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.sql.Timestamp;

public interface HistoryRepository extends JpaRepository<History, Long> {

    Page<History> findAll(Pageable pageable);

    @Query("SELECT h FROM History h " +
    "WHERE " +
    "(CAST(:startDate AS timestamp) IS NULL OR CAST(h.createdAt AS timestamp) IS NULL OR h.createdAt >= :startDate) AND " +
    "(CAST(:endDate AS timestamp) IS NULL OR CAST(h.createdAt AS timestamp) IS NULL OR h.createdAt <= :endDate) AND " +
    "(:type IS NULL OR h.type = :type) AND " +
    "(:action IS NULL OR h.action = :action) AND " +
            "(:userId IS NULL OR h.userId = :userId)"
    )
    Page<History> findByFiltering(@Param("startDate") @Nullable Timestamp startDate,
                                  @Param("endDate") @Nullable Timestamp endDate,
                                  @Param("type") @Nullable String type,
                                  @Param("action") @Nullable String action,
                                  @Param("userId") @Nullable Long userId,
                                  Pageable pageable);

    @Modifying
    @Query("DELETE FROM History h WHERE h.createdAt < :timestamp")
    void deleteOlderThan(Timestamp timestamp);
}
