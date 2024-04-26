package org.example.Xchange.services.xchangeServices;

import org.example.Xchange.models.FxRates;
import reactor.core.publisher.Mono;

public interface XchangeService {
    Mono<FxRates> getExchangeRates(String type);

}
