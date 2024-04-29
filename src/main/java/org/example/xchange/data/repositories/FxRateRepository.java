package org.example.Xchange.data.repositories;

import org.example.Xchange.data.models.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FxRateRepository extends JpaRepository<FxRate, Long> {
}
