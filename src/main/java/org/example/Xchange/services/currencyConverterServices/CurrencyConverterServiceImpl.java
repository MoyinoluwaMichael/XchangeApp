package org.example.Xchange.services.currencyConverterServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Xchange.dtos.request.CurrencyConverterRequest;
import org.example.Xchange.dtos.response.CurrencyConverterResponse;
import org.example.Xchange.exception.ValidationException;
import org.example.Xchange.models.FxRateListWrapper;
import org.example.Xchange.models.FxRateSingleWrapper;
import org.example.Xchange.models.FxRateWrapper;
import org.example.Xchange.services.httpServices.HttpRestService;
import org.example.Xchange.services.xchangeServices.XchangeService;
import org.example.Xchange.util.LoggingHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterServiceImpl implements CurrencyConverterService {
    private final HttpRestService httpRestService;
    private final LoggingHelper loggingHelper;
    private final XchangeService xchangeService;
    private final String EUR = "EUR";

    @Override
    public Mono<CurrencyConverterResponse> convertCurrency(CurrencyConverterRequest currencyConverterRequest) {
        validateRequestPayload(currencyConverterRequest);
        String currentDateInString = getCurrentDateInString();
        boolean isAnIntraConversion = currencyConverterRequest.getBaseCurrency().equalsIgnoreCase(EUR) || currencyConverterRequest.getTargetCurrency().equalsIgnoreCase(EUR);
        if (isAnIntraConversion) return this.processIntraConversion(currencyConverterRequest, currentDateInString);
        else return this.processInterConversion(currencyConverterRequest, currentDateInString);

    }

    private Mono<CurrencyConverterResponse> processInterConversion(
            CurrencyConverterRequest currencyConverterRequest, String currentDateInString
    ) {
        return this.getCurrencyRate(currencyConverterRequest.getBaseCurrency(), currencyConverterRequest.getRateType(), currentDateInString).flatMap(
                baseCurrencyRateResponse -> {
                    FxRateSingleWrapper baseCurrencyRate = (FxRateSingleWrapper) baseCurrencyRateResponse;
                    BigDecimal baseExchangeRate = BigDecimal.ONE.divide(baseCurrencyRate.getFxRates().getCurrencyAmountList().get(1).getAmount(), 50, RoundingMode.HALF_UP);

                    return this.getCurrencyRate(currencyConverterRequest.getTargetCurrency(), currencyConverterRequest.getRateType(), currentDateInString).flatMap(
                            targetCurrencyRateResponse -> {
                                FxRateSingleWrapper targetCurrencyRate = (FxRateSingleWrapper) targetCurrencyRateResponse;
                                BigDecimal targetExchangeRate = BigDecimal.ONE.divide(targetCurrencyRate.getFxRates().getCurrencyAmountList().get(1).getAmount(), 50, RoundingMode.HALF_UP);
                                BigDecimal exchangeRate = baseExchangeRate.divide(targetExchangeRate, 50, RoundingMode.HALF_UP);
                                BigDecimal conversionAmount = currencyConverterRequest.getAmount().multiply(exchangeRate).setScale(5, RoundingMode.HALF_UP);
                                FxRateListWrapper.FxRate fxRate = new FxRateListWrapper.FxRate();
                                fxRate.setDate(currentDateInString);
                                fxRate.setType(currencyConverterRequest.getRateType());
                                FxRateListWrapper.FxRate.CurrencyAmount baseCurrencyAmount = new FxRateListWrapper.FxRate.CurrencyAmount();
                                baseCurrencyAmount.setCurrency(currencyConverterRequest.getBaseCurrency());
                                baseCurrencyAmount.setAmount(BigDecimal.ONE);
                                FxRateListWrapper.FxRate.CurrencyAmount targetCurrencyAmount = new FxRateListWrapper.FxRate.CurrencyAmount();
                                targetCurrencyAmount.setCurrency(currencyConverterRequest.getTargetCurrency());
                                targetCurrencyAmount.setAmount(exchangeRate.setScale(5, RoundingMode.HALF_UP));
                                fxRate.setCurrencyAmountList(List.of(baseCurrencyAmount, targetCurrencyAmount));
                                return Mono.just(
                                        CurrencyConverterResponse.builder()
                                                .baseCurrency(currencyConverterRequest.getBaseCurrency())
                                                .targetCurrency(currencyConverterRequest.getTargetCurrency())
                                                .amount(currencyConverterRequest.getAmount())
                                                .rate(fxRate)
                                                .conversionAmount(conversionAmount)
                                                .build()
                                );
                            }
                    );
                }
        );
    }

    private Mono<CurrencyConverterResponse> processIntraConversion(
            CurrencyConverterRequest currencyConverterRequest, String currentDateInString
    ) {
        boolean baseCurrencyIsEuro = currencyConverterRequest.getBaseCurrency().equalsIgnoreCase(EUR);
        Mono<FxRateWrapper> currencyRateMono = baseCurrencyIsEuro ?
                getCurrencyRate(currencyConverterRequest.getTargetCurrency(), currencyConverterRequest.getRateType(), currentDateInString) :
                getCurrencyRate(currencyConverterRequest.getBaseCurrency(), currencyConverterRequest.getRateType(), currentDateInString);

        return currencyRateMono.flatMap(
                genericCurrencyRate -> {
                    FxRateSingleWrapper currencyRate = (FxRateSingleWrapper) genericCurrencyRate;
                    BigDecimal baseRate = BigDecimal.ZERO;
                    BigDecimal targetRate = BigDecimal.ZERO;
                    for (FxRateListWrapper.FxRate.CurrencyAmount currencyAmount : currencyRate.getFxRates().getCurrencyAmountList()) {
                        if (currencyAmount.getCurrency().equalsIgnoreCase(currencyConverterRequest.getBaseCurrency())) {
                            baseRate = currencyAmount.getAmount();
                        }else {
                            targetRate = currencyAmount.getAmount();
                        }
                    }
                    BigDecimal conversionAmount = currencyConverterRequest.getAmount().multiply (targetRate.divide(baseRate, 50, RoundingMode.HALF_UP)).setScale(5, RoundingMode.HALF_UP);
                    return Mono.just(
                            CurrencyConverterResponse.builder()
                                    .baseCurrency(currencyConverterRequest.getBaseCurrency())
                                    .targetCurrency(currencyConverterRequest.getTargetCurrency())
                                    .amount(currencyConverterRequest.getAmount())
                                    .rate(currencyRate.getFxRates())
                                    .conversionAmount(conversionAmount)
                                    .build()
                    );
                }
        );
    }

    private String getCurrentDateInString() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDate.format(formatter);
    }

    private void validateRequestPayload(CurrencyConverterRequest currencyConverterRequest) {
        if (
                currencyConverterRequest.getBaseCurrency() == null ||
                        currencyConverterRequest.getTargetCurrency() == null ||
                        currencyConverterRequest.getAmount() == null ||
                        currencyConverterRequest.getRateType() == null
        ) throw new ValidationException("All request fields are required");
    }

    private Mono<FxRateWrapper> getCurrencyRate(String currency, String rateType, String currentDateInString) {
        return xchangeService.getFxRatesForCurrency(rateType, currency, currentDateInString, currentDateInString);
    }
}
