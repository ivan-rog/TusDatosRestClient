package com.tusdatos.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "configuration.tusdatos")
@Getter
@Setter
public class TusDatosProperties {

    private String url;
    private String user;
    private String password;
    private String endpointJobLaunch;
    private String endpointJobStatus;
    private String endpointJobRetry;
    private String endpointReportJson;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration jobStatusInitialDelaySeconds;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration jobStatusPollingIntervalSeconds;
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration jobStatusTimeoutSeconds;
    private List<String> retryErrors;
    private int numberOfRetries;

}
