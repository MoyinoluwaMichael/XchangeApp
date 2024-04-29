package org.example.Xchange.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Xchange.data.models.FxRate;
import org.example.Xchange.data.models.FxRateListWrapper;
import org.example.Xchange.data.repositories.FxRateRepository;
import org.example.Xchange.services.xchangeServices.XchangeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FxRateJobsService {
    private final XchangeService xchangeService;
    private final FxRateRepository fxRateRepository;

//    @Scheduled(cron = "0 * * * * ?") // Scheduled for every minute
    @Scheduled(cron = "0 0 0 * * ?") // Scheduled for 12am daily
    public void scheduleFxRateRetrieval(){
        String LT = "lt";
        fetchCurrentRateFor(LT);
        String EU = "eu";
        fetchCurrentRateFor(EU);
    }

    private void fetchCurrentRateFor(String rateType) {
        xchangeService.getCurrentExchangeRates(rateType).map(
                currentRatesResponse -> {
                    FxRateListWrapper currentRates = (FxRateListWrapper) currentRatesResponse;
                    for (FxRateListWrapper.FxRate fxRate: currentRates.getFxRates()) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        try {
                            FxRate persistableFxRate = objectMapper.readValue(objectMapper.writeValueAsString(fxRate), FxRate.class);
                            FxRate savedRate = fxRateRepository.save(persistableFxRate);
                            log.info("FX Rate Saved::>> "+ objectMapper.writeValueAsString(savedRate));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                    return currentRates;
                }
        ).subscribe();
    }

}
