package org.example.xchange.services.xchangeServices;

import org.example.xchange.data.models.FxRateWrapper;
import reactor.core.publisher.Mono;

public interface XchangeService {
    Mono<FxRateWrapper> getCurrentExchangeRates(String type);

    Mono<FxRateWrapper> getRatesForSpecifiedDate(String type, String date);

    Mono<FxRateWrapper> getFxRatesForCurrency(String type, String currency, String dateFrom, String dateTo);
}
