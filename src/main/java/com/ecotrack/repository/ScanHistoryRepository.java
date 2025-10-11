package com.ecotrack.repository;

import com.ecotrack.domain.ScanHistory;
import com.ecotrack.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ScanHistoryRepository extends JpaRepository<ScanHistory, UUID> {
  List<ScanHistory> findByUserOrderByScannedAtDesc(UserAccount user);
  List<ScanHistory> findByUserAndScannedAtBetweenOrderByScannedAtDesc(UserAccount user, OffsetDateTime start, OffsetDateTime end);
}
