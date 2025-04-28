package com.tusdatos.bussines;

import com.tusdatos.mocks.JobLaunchMock;
import com.tusdatos.mocks.JobReportJson;
import com.tusdatos.mocks.JobRetryMock;
import com.tusdatos.mocks.JobStatusMock;
import com.tusdatos.utils.JacksonUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.test.StepVerifier;

import java.io.IOException;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class TusDatosServiceTest {

    private static MockWebServer mockWebServer;
    @Autowired
    private TusDatosService tusDatosService;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(9090);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @Tag("tus_datos")
    void when_process_the_CC_111_document() {
        this.enqueueResponseCC111();
        StepVerifier.create(this.tusDatosService.processDocuments(JobLaunchMock.launchRequestCC111()))
                .assertNext(response ->
                        {
                            try {
                                JSONAssert.assertEquals(JobReportJson.reportJsonCC111(), JacksonUtils.objectToJson(response), true);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
                .verifyComplete();
    }

    private void enqueueResponseCC111() {
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(JobLaunchMock.launchResponseCC111())
        );
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(JobStatusMock.jobStatusCC11WithError())
        );
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(JobRetryMock.jobRetryCC111())
        );
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value())
                .setBody(JobStatusMock.jobStatusCC11())
        );
        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json").
                setResponseCode(HttpStatus.OK.value())
                .setBody(JobReportJson.reportJsonCC111())
        );
    }

    @Test
    @Tag("tus_datos")
    void when_the_service_has_timeout() {
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));
        StepVerifier.create(this.tusDatosService.processDocuments(JobLaunchMock.launchRequestCC111()))
                .expectErrorMatches(Exceptions::isRetryExhausted)
                .verify();
    }

    @Test
    @Tag("tus_datos")
    void when_the_service_returns_a_500_error() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        StepVerifier.create(this.tusDatosService.processDocuments(JobLaunchMock.launchRequestCC111()))
                .expectErrorMatches(Exceptions::isRetryExhausted)
                .verify();
    }

    @Test
    @Tag("tus_datos")
    void when_the_service_returns_a_401_error() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(HttpStatus.UNAUTHORIZED.value()));
        StepVerifier.create(this.tusDatosService.processDocuments(JobLaunchMock.launchRequestCC111()))
                .expectErrorMatches(throwable -> ((WebClientResponseException) throwable).getStatusCode().is4xxClientError())
                .verify();
    }

}