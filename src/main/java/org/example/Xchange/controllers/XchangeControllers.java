package org.example.Xchange.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.Xchange.dtos.response.base.RestApiResponse;
import org.example.Xchange.dtos.response.base.transformer.ResponseAssembler;
import org.example.Xchange.models.FxRates;
import org.example.Xchange.services.xchangeServices.XchangeService;
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
    @GetMapping("/currentRates")
    public ResponseEntity<Mono<RestApiResponse<FxRates>>> getCurrentExchangeRates(
            @RequestParam(name = "type") String type
    ) {

        Mono<FxRates> exchangeRatesResponseMono = xchangeService.getCurrentExchangeRates(type);

        return ResponseEntity.ok(
                Mono.zip(exchangeRatesResponseMono, exchangeRatesResponseMono,
                        (client, ctx) -> ResponseAssembler.toResponse(HttpStatus.OK, client, "Exchange rates retrieved successfully"))
        );

    }

    @Operation(summary = "Retrieve exchange rates for the specified date")
    @GetMapping("/rates")
    public ResponseEntity<Mono<RestApiResponse<FxRates>>> getRatesForSpecifiedDate(
            @RequestParam(name = "type") String type,
            @RequestParam(name = "date") String date
    ) {

        Mono<FxRates> exchangeRatesResponseMono = xchangeService.getRatesForSpecifiedDate(type, type);

        return ResponseEntity.ok(
                Mono.zip(exchangeRatesResponseMono, exchangeRatesResponseMono,
                        (client, ctx) -> ResponseAssembler.toResponse(HttpStatus.OK, client, "Exchange rates retrieved successfully"))
        );

    }

}
