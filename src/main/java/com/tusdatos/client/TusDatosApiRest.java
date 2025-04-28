package com.tusdatos.client;

import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;

@Slf4j
@Service
public final class TusDatosApiRest implements ApiRestClient {

    private final WebClient webClient;

    public TusDatosApiRest(final WebClient webClient, ExchangeFilterFunction authentication, @Value("${configuration.tusdatos.url}") String urlBase) {
        this.webClient = webClient.mutate().baseUrl(urlBase).filter(authentication).build();
    }

    @Override
    public <T> Mono<T> get(String uri, Class<T> responseType) {
        return this.additional(this.webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType)
        );
    }

    @Override
    public <T, V> Mono<T> post(String uri, V body, Class<T> responseType) {
        return this.additional(this.webClient
                .post()
                .uri(uri)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
        );
    }

    private <T> Mono<T> additional(Mono<T> request) {
        return request.transform(mono ->
                mono.retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(throwable ->
                                        switch (throwable) {
                                            case WebClientRequestException e ->
                                                    e.getCause() instanceof ConnectException ||
                                                            e.getCause() instanceof TimeoutException;
                                            case WebClientResponseException e ->
                                                    e.getStatusCode().is5xxServerError()
                                                            || e.getCause() instanceof TimeoutException;
                                            case TimeoutException ignored -> true;
                                            case ConnectTimeoutException ignored -> true;
                                            default -> false;
                                        }
                                )
                                .doAfterRetry(retrySignal -> log.warn("Retrying request: {} times, by: {}", retrySignal.totalRetries(), retrySignal.failure().getMessage())))
                        .doOnError(ex -> log.error("Error: ", ex)).log()
        );
    }
}
