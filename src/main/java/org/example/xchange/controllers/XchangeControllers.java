package org.example.xchange.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.xchange.dtos.response.base.RestApiResponse;
import org.example.xchange.dtos.response.base.transformer.ResponseAssembler;
import org.example.xchange.data.models.FxRateWrapper;
import org.example.xchange.services.xchangeServices.XchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequestMapping("/xchange")
@RestController
@RequiredArgsConstructor
public class XchangeControllers {
    private final XchangeService xchangeService;

    @Operation(summary = "Retrieve current exchange rates")
    @GetMapping("/getCurrentExchangeRates")
    public ResponseEntity<Mono<RestApiResponse<FxRateWrapper>>> getCurrentExchangeRates(
            @RequestParam(name = "type", required = false) String type
    ) {

        Mono<FxRateWrapper> exchangeRatesResponseMono = xchangeService.getCurrentExchangeRates(type);

        return ResponseEntity.ok(
                Mono.zip(exchangeRatesResponseMono, exchangeRatesResponseMono,
                        (client, ctx) -> ResponseAssembler.toResponse(HttpStatus.OK, client, "Exchange rates retrieved successfully"))
        );

    }

    @Operation(summary = "Retrieve exchange rates for the specified date")
    @GetMapping("/getRatesForSpecifiedDate")
    public ResponseEntity<Mono<RestApiResponse<FxRateWrapper>>> getRatesForSpecifiedDate(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "date") String date
    ) {

        Mono<FxRateWrapper> exchangeRatesResponseMono = xchangeService.getRatesForSpecifiedDate(type, date);

        return ResponseEntity.ok(
                Mono.zip(exchangeRatesResponseMono, exchangeRatesResponseMono,
                        (client, ctx) -> ResponseAssembler.toResponse(HttpStatus.OK, client, "Exchange rates retrieved successfully"))
        );

    }


    @Operation(summary = "Retrieve Exchange Rates between specified dates")
    @GetMapping("/getFxRatesForCurrency")
    public ResponseEntity<Mono<RestApiResponse<FxRateWrapper>>> getFxRatesForCurrency(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "currency") String currency,
            @RequestParam(name = "dateFrom") String dateFrom,
            @RequestParam(name = "dateTo") String dateTo
    ) {

        Mono<FxRateWrapper> exchangeRatesResponseMono = xchangeService.getFxRatesForCurrency(type, currency, dateFrom, dateTo);

        return ResponseEntity.ok(
                Mono.zip(exchangeRatesResponseMono, exchangeRatesResponseMono,
                        (client, ctx) -> ResponseAssembler.toResponse(HttpStatus.OK, client, "Exchange rates retrieved successfully"))
        );

    }

}
