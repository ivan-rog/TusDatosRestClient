package com.tusdatos.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientConfig {

    private ConnectionProvider getConnectionProvider() {
        return ConnectionProvider
                .builder("custom-http-client")
                .maxConnections(50)
                .maxIdleTime(Duration.ofSeconds(10))
                .maxLifeTime(Duration.ofSeconds(20))
                .pendingAcquireTimeout(Duration.ofSeconds(30))
                .evictInBackground(Duration.ofSeconds(120))
                .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.create(getConnectionProvider())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5_000)
                .doOnConnected(
                        connection ->
                                connection.addHandlerLast(new ReadTimeoutHandler(5))
                                        .addHandlerLast(new WriteTimeoutHandler(5)))
                .responseTimeout(Duration.ofSeconds(5));
    }

    @Bean
    public WebClient createWebClient() {
        return WebClient.builder()
                .filters(
                        exchangeFilterFunctions -> {
                            exchangeFilterFunctions.add(this.logRequest());
                            exchangeFilterFunctions.add(this.logResponse());
                        }
                )
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(createHttpClient()))
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("header request {} - {}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response: {} {}", clientResponse.request().getMethod(), clientResponse.request().getURI());
            clientResponse.headers().
                    asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("header response {} - {}", name, value)));
            log.info("Status Code: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

}
