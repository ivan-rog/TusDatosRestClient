package com.tusdatos.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Configuration
public class BasicAuthenticationConfig {

    @Bean
    public ExchangeFilterFunction basicAuth(com.tusdatos.configuration.properties.TusDatosProperties tusDatosProperties) {
        return basicAuthentication(tusDatosProperties.getUser(), tusDatosProperties.getPassword());
    }
}
