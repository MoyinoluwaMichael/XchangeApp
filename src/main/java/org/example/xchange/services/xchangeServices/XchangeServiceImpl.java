package org.example.Xchange.services.xchangeServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Xchange.exception.ValidationException;
import org.example.Xchange.data.models.FxRateWrapper;
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
    public Mono<FxRateWrapper> getCurrentExchangeRates(String type) {
        validateRateType(type);
        String endpointPath = "/getCurrentFxRates?tp=" + type;
        URI uri = httpRestService.constructUriForEndpoint(endpointPath);
        return this.httpRestService.get(uri)
                .map(getExchangeRatesResponse -> {
                    log.info("Exchange Rates Retrieved Successfully ::");
                    loggingHelper.logRequest(getExchangeRatesResponse);
                    return getExchangeRatesResponse;
                });
    }

    private void validateRateType(String type) {
        if (type != null && (!type.equalsIgnoreCase("lt") && !type.equalsIgnoreCase("eu"))){
            throw new ValidationException("Invalid exchange rate type - ltd. Valid values are LT and EU only. If not provided - value LT is assumed.");
        }
    }

    @Override
    public Mono<FxRateWrapper> getRatesForSpecifiedDate(String type, String date) {
        validateRateType(type);
        validateRateDate(date);
        String endpointPath = "/getFxRates?tp=" + type+"&dt="+date;
        URI uri = httpRestService.constructUriForEndpoint(endpointPath);
        return this.httpRestService.get(uri)
                .map(getExchangeRatesResponse -> {
                    log.info("Exchange Rates Retrieved Successfully ::");
                    loggingHelper.logRequest(getExchangeRatesResponse);
                    return getExchangeRatesResponse;
                });
    }

    private void validateRateDate(String date) {
        if (date == null || date.split("-").length < 3 || date.split("-")[0].length() < 4){
            throw new ValidationException("Invalid date - 2015/01/03. Expecting date in ISO 8601 format.");
        }
    }

    @Override
    public Mono<FxRateWrapper> getFxRatesForCurrency(String type, String currency, String dateFrom, String dateTo) {
        validateRateType(type);
        validateRateDate(dateFrom);
        validateRateDate(dateTo);
        String getFxRatesForCurrencyEndpoint = "/getFxRatesForCurrency?tp=%s&ccy=%s&dtFrom=%s&dtTo=%s";
        String endpointPath = String.format(getFxRatesForCurrencyEndpoint, type, currency, dateFrom, dateTo);
        URI uri = httpRestService.constructUriForEndpoint(endpointPath);
        return this.httpRestService.get(uri)
                .map(getExchangeRatesResponse -> {
                    log.info("Exchange Rates Retrieved Successfully ::");
                    loggingHelper.logRequest(getExchangeRatesResponse);
                    return getExchangeRatesResponse;
                });
    }
}
