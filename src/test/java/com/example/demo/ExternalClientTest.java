package com.example.demo;

import com.exchangerate.client.ExternalExchangeRateClient;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

public class ExternalClientTest {

    @Test
    void testExchangeRateApi() {

        WebClient webClient = WebClient.builder().build();

        ExternalExchangeRateClient exchangeClient =
                new ExternalExchangeRateClient(webClient);

        StepVerifier.create(
                        exchangeClient.getExchangeRates("USD", List.of("KRW", "JPY", "EUR"))
                )
                .expectNextMatches(result -> {
                    System.out.println("API RESULT = " + result);
                    return result != null && !result.isEmpty();
                })
                .verifyComplete();
    }
}
