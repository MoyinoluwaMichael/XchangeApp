package org.example.xchange.services.currencyConverterServices;

import org.example.xchange.dtos.request.CurrencyConverterRequest;
import org.example.xchange.dtos.response.CurrencyConverterResponse;
import reactor.core.publisher.Mono;

public interface CurrencyConverterService {
    Mono<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest currencyConverterRequest);
}
