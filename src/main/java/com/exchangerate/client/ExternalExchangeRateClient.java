package com.exchangerate.client;

import com.exchangerate.dto.ExchangeRateResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class ExternalExchangeRateClient {
    private final WebClient client;

    //API_KEY
    private static final String API_KEY = "3cc5b87a4c774e6cee1bc67ec962a2b2";
    //API_URL
    private static final String URL = "https://open.er-api.com/v6/latest/USD";
            //"http://api.exchangerate.host/latest?base=USD&symbols=KRW,JPY,EUR";
/*            URL = "https://api.exchangerate.host/live?access_key="
            +API_KEY+"&format=1";*/

    public ExternalExchangeRateClient(WebClient client){
        this.client = client;
    }

    public Mono<Map<String, Double>> getExchangeRates(String base, List<String> targets) {

        return client.get()
                .uri(URL)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .map(ExchangeRateResponse::getRates);
//                        .map(response -> {
//            Map<String, Double> allRates = response.getRates();
//
//            // KRW, JPY, EUR만 추출
//            return Map.of(
//                    "KRW", allRates.get("KRW"),
//                    "JPY", allRates.get("JPY"),
//                    "EUR", allRates.get("EUR")
//            );
//        });
    }
}
