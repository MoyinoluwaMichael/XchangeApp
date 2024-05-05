package org.example.xchange.data.repositories;

import org.example.xchange.data.models.FxRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FxRateRepository extends JpaRepository<FxRate, Long>, JpaSpecificationExecutor<FxRate> {

//    @Query("SELECT fx FROM FxRate fx JOIN fx.currencyAmountList ca " +
//            "WHERE fx.type = :type " +
//            "AND ca.currency = :currency " +
//            "AND fx.date BETWEEN :dateFrom AND :dateTo")
//    List<FxRate> findByTypeAndCurrencyAndDateBetween(
//            @Param("type") String type,
//            @Param("currency") String currency,
//            @Param("dateFrom") String dateFrom,
//            @Param("dateTo") String dateTo
//    );
}
