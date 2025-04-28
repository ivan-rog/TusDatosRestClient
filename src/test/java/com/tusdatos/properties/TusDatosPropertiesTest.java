package com.tusdatos.properties;

import com.tusdatos.configuration.properties.TusDatosProperties;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TusDatosPropertiesTest {
    @Autowired
    private TusDatosProperties tusDatosProperties;

    @Test
    @Tag("tus_datos")
    void testTusDatosProperties() {
        assertNotNull(tusDatosProperties);
        assertEquals("http://localhost:9090", tusDatosProperties.getUrl());
        assertEquals("user", tusDatosProperties.getUser());
        assertEquals("pass", tusDatosProperties.getPassword());
        assertEquals("/api/launch", tusDatosProperties.getEndpointJobLaunch());
        assertEquals("/api/results/{jobkey}", tusDatosProperties.getEndpointJobStatus());
        assertEquals("/api/retry/{id}?typedoc={typedoc}", tusDatosProperties.getEndpointJobRetry());
        assertEquals("/api/report_json/{id}", tusDatosProperties.getEndpointReportJson());
        assertEquals(5, tusDatosProperties.getJobStatusInitialDelaySeconds().getSeconds());
        assertEquals(1, tusDatosProperties.getJobStatusPollingIntervalSeconds().getSeconds());
        assertEquals(30, tusDatosProperties.getJobStatusTimeoutSeconds().getSeconds());
        assertFalse(tusDatosProperties.getRetryErrors().isEmpty());
    }
}