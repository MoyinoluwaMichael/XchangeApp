package org.example.xchange.services.currencyService;

import lombok.RequiredArgsConstructor;
import org.example.xchange.data.models.CurrencyList;
import org.example.xchange.data.models.FxRateListWrapper;
import org.example.xchange.services.xchangeServices.XchangeService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService{
    private final XchangeService xchangeService;

    @Override
    public Mono<CurrencyList> getCurrencyList(String rateType) {
        CurrencyList.Currency euro = new CurrencyList.Currency();
        euro.setIsoCode("EUR");
        List<CurrencyList.Currency> currencies = new ArrayList<>();
        currencies.add(euro);
        return xchangeService.getCurrentExchangeRates(rateType).flatMap(
                currencyListResponse -> {
                    FxRateListWrapper fxRateListWrapper = (FxRateListWrapper) currencyListResponse;
                    for (var fxRate: fxRateListWrapper.getFxRates()) {
                        CurrencyList.Currency currency = new CurrencyList.Currency();
                        currency.setIsoCode(fxRate.getCurrencyAmountList().get(1).getCurrency().toUpperCase(Locale.ROOT));
                        currencies.add(currency);
                        System.out.println("CURRENCY;;?? "+currency);
                    }
                    CurrencyList currencyList = new CurrencyList();
                    currencies.sort(Comparator.comparing(CurrencyList.Currency::getIsoCode));
                    currencyList.setCurrencies(currencies);
                    return Mono.just(currencyList);
                }
        );
    }
}
