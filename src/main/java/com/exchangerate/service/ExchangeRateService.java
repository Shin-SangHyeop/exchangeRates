package com.exchangerate.service;

import com.exchangerate.client.ExternalExchangeRateClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateService {
    private final ExternalExchangeRateClient client;

    public ExchangeRateService(ExternalExchangeRateClient client){
        this.client = client;
    }

    public Mono<Map<String, Double>> getCurrentRates() {
        return client.getExchangeRates("USD", List.of("KRW", "JPY", "EUR"))
                .map(rates -> {
                    Map<String, Double> filtered = new HashMap<>();
                    filtered.put("KRW", rates.get("KRW"));
                    filtered.put("JPY", rates.get("JPY"));
                    filtered.put("EUR", rates.get("EUR"));
                    return filtered;
                });
    }


}
