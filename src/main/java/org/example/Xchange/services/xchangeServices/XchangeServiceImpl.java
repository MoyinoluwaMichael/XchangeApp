package org.example.Xchange.services.xchangeServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Xchange.exception.ValidationException;
import org.example.Xchange.models.FxRates;
import org.example.Xchange.services.httpServices.HttpRestService;
import org.example.Xchange.util.LoggingHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
@RequiredArgsConstructor
@Slf4j
public class XchangeServiceImpl implements XchangeService{
    private final HttpRestService httpRestService;
    private final LoggingHelper loggingHelper;

    @Override
    public Mono<FxRates> getExchangeRates(String type) {
        if (type != null && (!type.equalsIgnoreCase("lt") || !type.equalsIgnoreCase("eu"))){
            throw new ValidationException("Invalid exchange rate type - ltd. Valid values are LT and EU only. If not provided - value LT is assumed.");
        }
        String endpointPath = "/getCurrentFxRates?tp=" + type;
        URI uri = httpRestService.constructUriForEndpoint(endpointPath);
        return this.httpRestService.get(uri, FxRates.class)
                .map(getExchangeRatesResponse -> {
                    log.info("Exchange Rates Retrieved Successfully ::");
                    loggingHelper.logRequest(getExchangeRatesResponse);
                    return getExchangeRatesResponse;
                });
    }
}
