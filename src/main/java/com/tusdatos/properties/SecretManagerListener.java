package com.tusdatos.properties;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class SecretManagerListener {

    private TusDatosProperties tusDatosProperties;

    @Scheduled(fixedDelay = 5000)
    public void refreshProperties() {
        // This method will be called every 5 seconds to refresh the properties
        // You can add your logic here to fetch the latest properties from Secret Manager
        log.info("Refreshing properties...");
        log.info("TusDatos URL: {}", tusDatosProperties.getUrl());
        log.info("TusDatos User: {}", tusDatosProperties.getUser());
        log.info("TusDatos Password: {}", tusDatosProperties.getPassword());
    }
}
