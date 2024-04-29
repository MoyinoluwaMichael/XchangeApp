package org.example.Xchange.services.currencyConverterServices;

import org.example.Xchange.dtos.request.CurrencyConverterRequest;
import org.example.Xchange.dtos.response.CurrencyConverterResponse;
import reactor.core.publisher.Mono;

public interface CurrencyConverterService {
    Mono<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest currencyConverterRequest);
}
