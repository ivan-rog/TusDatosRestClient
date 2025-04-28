package com.tusdatos.client;

import reactor.core.publisher.Mono;

public interface ApiRestClient {

    <T> Mono<T> get(final String uri, final Class<T> responseType);

    <T, V> Mono<T> post(final String uri, V body, final Class<T> responseType);
}
