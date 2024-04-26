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

    @Operation(summary = "Retrieve exchange rates")
    @GetMapping("/rates")
    public ResponseEntity<Mono<RestApiResponse<FxRates>>> getExchangeRates(
            @RequestParam(name = "type") String type
    ) {

        Mono<FxRates> exchangeRatesResponseMono = xchangeService.getExchangeRates(type);

        return ResponseEntity.ok(
                Mono.zip(exchangeRatesResponseMono, exchangeRatesResponseMono,
                        (client, ctx) -> ResponseAssembler.toResponse(HttpStatus.OK, client, "Exchange rates retrieved successfully"))
        );

    }

}
