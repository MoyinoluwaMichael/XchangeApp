package org.example.Xchange.services.xchangeServices;

import org.example.Xchange.models.FxRates;
import reactor.core.publisher.Mono;

public interface XchangeService {
    Mono<FxRates> getCurrentExchangeRates(String type);

    Mono<FxRates> getRatesForSpecifiedDate(String type, String date);
}
