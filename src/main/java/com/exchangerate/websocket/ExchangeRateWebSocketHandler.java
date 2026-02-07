package com.exchangerate.websocket;

import com.exchangerate.service.ExchangeRateService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketHandler;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.socket.WebSocketMessage;

import java.time.Duration;

@Component
public class ExchangeRateWebSocketHandler implements WebSocketHandler {

    private final ExchangeRateService service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExchangeRateWebSocketHandler(ExchangeRateService service){
        this.service = service;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(Flux.interval(Duration.ZERO, Duration.ofSeconds(5)).flatMap(tick -> service.getCurrentRates()).map(rates -> {
            try {
                String json = objectMapper.writeValueAsString(rates);
                return session.textMessage(json);
            } catch (Exception e) {
                return session.textMessage("{\"error\":\"json parse error\"}");
            }
        }));
/*        return session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .filter(msg -> msg.equals("REQUEST_RATES"))
                .flatMap(msg ->
                        service.getCurrentRates()
                                .map(rates -> {
                                    try {
                                        String json = objectMapper.writeValueAsString(rates);
                                        return session.textMessage(json);
                                    } catch (Exception e) {
                                        return session.textMessage("{\"error\":\"json parse error\"}");
                                    }
                                })
                                .flatMap(wsMsg -> session.send(Mono.just(wsMsg)))
                )
                .then();*/
    }
}
