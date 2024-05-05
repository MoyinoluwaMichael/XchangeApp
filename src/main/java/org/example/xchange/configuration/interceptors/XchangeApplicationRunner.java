package org.example.xchange.configuration.interceptors;

import lombok.RequiredArgsConstructor;
import org.example.xchange.jobs.FxRateJobsService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class XchangeApplicationRunner implements ApplicationRunner {
    private final FxRateJobsService fxRateJobsService;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        fxRateJobsService.retrieveCurrentLithuaniaData();
    }


}
