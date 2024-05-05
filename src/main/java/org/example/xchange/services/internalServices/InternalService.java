package org.example.xchange.services.internalServices;

import org.example.xchange.data.models.FxRate;
import org.example.xchange.data.models.FxRateWrapper;
import reactor.core.publisher.Mono;

public interface InternalService {
    FxRate save(FxRate persistableFxRate);

    FxRateWrapper getLithuaniaFxRatesHistoryForCurrency(String type, String currency, String dateFrom, String dateTo);
}
