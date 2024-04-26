package org.example.Xchange.services.xchangeServices;

import org.example.Xchange.models.FxRateWrapper;
import reactor.core.publisher.Mono;

public interface XchangeService {
    Mono<FxRateWrapper> getCurrentExchangeRates(String type);

    Mono<FxRateWrapper> getRatesForSpecifiedDate(String type, String date);

    Mono<FxRateWrapper> getFxRatesForCurrency(String type, String currency, String dateFrom, String dateTo);
}
