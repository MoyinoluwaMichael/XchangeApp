package org.example.xchange.services.internalServices;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.xchange.data.models.FxRate;
import org.example.xchange.data.models.FxRateListWrapper;
import org.example.xchange.data.models.FxRateSingleWrapper;
import org.example.xchange.data.models.FxRateWrapper;
import org.example.xchange.data.repositories.FxRateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InternalServiceImpl implements InternalService {
    private final FxRateRepository fxRateRepository;


    @Override
    public FxRate save(FxRate fxRate) {
        return fxRateRepository.save(fxRate);
    }

    @Override
    public FxRateWrapper getLithuaniaFxRatesHistoryForCurrency(String type, String currency, String dateFrom, String dateTo) {
        List<FxRate> internalFxRates = fxRateRepository.findAll();
        List<FxRate> fxRates = new ArrayList<>();
        for (var each : internalFxRates) {
            if (each.getType().equalsIgnoreCase(type) && each.getCurrencyAmountList().get(1).getCurrency().equalsIgnoreCase(currency)) {
                fxRates.add(each);
                break;
            }
        }
        FxRateListWrapper ratesWithAListWrapper = new FxRateListWrapper();
        FxRateSingleWrapper ratesWithASingleWrapper = new FxRateSingleWrapper();

        List<FxRateListWrapper.FxRate> mappedRates = fxRates.stream()
                .map(fxRate -> {
                    FxRateListWrapper.FxRate wrapperRate = new FxRateListWrapper.FxRate();
                    wrapperRate.setType(fxRate.getType());
                    wrapperRate.setDate(fxRate.getDate());
                    List<FxRateListWrapper.FxRate.CurrencyAmount> ccyAmts = new ArrayList<>();
                    wrapperRate.setCurrencyAmountList(
                            fxRate.getCurrencyAmountList().stream().map(
                                    currencyAmount -> {
                                        FxRateListWrapper.FxRate.CurrencyAmount ccyAmt = null;
                                        for (var each : fxRate.getCurrencyAmountList()) {
                                            ccyAmt = new FxRateListWrapper.FxRate.CurrencyAmount();
                                            ccyAmt.setCurrency(each.getCurrency());
                                            ccyAmt.setAmount(each.getAmount());
                                            ccyAmts.add(ccyAmt);
                                        }
                                        wrapperRate.setCurrencyAmountList(ccyAmts);
                                        return ccyAmt;
                                    }
                            ).toList()
                    );
                    return wrapperRate;
                })
                .collect(Collectors.toList());

        ratesWithAListWrapper.setFxRates(mappedRates);

        if (fxRates.size() > 1) {
            ratesWithAListWrapper.setFxRates(mappedRates);
            return ratesWithAListWrapper;
        } else if (fxRates.size() == 1) {
            ratesWithASingleWrapper.setFxRates(mappedRates.get(0));
            return ratesWithASingleWrapper;
        } else return ratesWithASingleWrapper;
    }
}
