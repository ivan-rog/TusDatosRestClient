package com.tusdatos.client;

import com.tusdatos.configuration.properties.TusDatosProperties;
import com.tusdatos.dto.client.response.JobLaunchResponseDTO;
import com.tusdatos.dto.client.response.JobStatusResponseDTO;
import com.tusdatos.mocks.JobLaunchMock;
import com.tusdatos.mocks.JobStatusMock;
import com.tusdatos.utils.JacksonUtils;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class TusDatosApiRestTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private TusDatosApiRest tusDatosApiRest;

    @Autowired
    private TusDatosProperties tusDatosProperties;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(9090);
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testLaunchJob() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.SC_OK)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(JobLaunchMock.launchResponseCC111()));
        StepVerifier.create(tusDatosApiRest.post(tusDatosProperties.getEndpointJobLaunch(), JobLaunchMock.launchRequestCC111(), JobLaunchResponseDTO.class))
                .assertNext(response -> {
                    assertNotNull(response);
                    try {
                        JSONAssert.assertEquals(JobLaunchMock.launchResponseCC111().trim(), JacksonUtils.objectToJson(response), true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    @Test
    void testJobStatus() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.SC_OK)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody(JobStatusMock.jobStatusCC11()));
        StepVerifier.create(
                tusDatosApiRest.get(
                        tusDatosProperties.getEndpointJobStatus().replace("{jobkey}", "1"),
                        JobStatusResponseDTO.class)
                )
                .assertNext(response -> {
                    assertNotNull(response);
                    assertEquals(JobStatusMock.jobStatusCC11().trim(), JacksonUtils.objectToJson(response));
                    try {
                        JSONAssert.assertEquals(JobStatusMock.jobStatusCC11().trim(), JacksonUtils.objectToJson(response), true);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                })
                .verifyComplete();
    }

    @Test
    void testLaunchJobWithError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.SC_BAD_REQUEST)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"error\":\"Bad Request\"}"));
        StepVerifier.create(tusDatosApiRest.post(tusDatosProperties.getEndpointJobLaunch(), JobLaunchMock.launchRequestCC111(), JobLaunchResponseDTO.class))
                .expectErrorMatches(throwable -> {
                    WebClientResponseException webClientResponseException = (WebClientResponseException) throwable;
                    return webClientResponseException.getStatusCode().is4xxClientError();
                })
                .verify();
    }

    void testLaunchJobWithError2() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"error\":\"Bad Request\"}"));
        StepVerifier.create(tusDatosApiRest.post(tusDatosProperties.getEndpointJobLaunch(), JobLaunchMock.launchRequestCC111(), JobLaunchResponseDTO.class))
                .expectErrorMatches(Exceptions::isRetryExhausted)
                .verify();
    }

    @Test
    void testLaunchJobWithTimeout() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE)
                .setResponseCode(HttpStatus.SC_REQUEST_TIMEOUT)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"error\":\"Bad Request\"}"));
        StepVerifier.create(tusDatosApiRest.post(tusDatosProperties.getEndpointJobLaunch(), JobLaunchMock.launchRequestCC111(), JobLaunchResponseDTO.class))
                .expectErrorMatches(Exceptions::isRetryExhausted)
                .verify();
    }
}