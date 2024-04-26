package org.example.Xchange.dtos.response;

import lombok.*;
import org.example.Xchange.models.FxRateListWrapper;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class CurrencyConverterResponse implements Serializable {
    private String baseCurrency;
    private String targetCurrency;
    private BigDecimal amount;
    private BigDecimal conversionAmount;
    private FxRateListWrapper.FxRate rate;
}
