package com.tusdatos.bussines;

import com.tusdatos.client.ApiRestClient;
import com.tusdatos.configuration.properties.TusDatosProperties;
import com.tusdatos.dto.client.request.JobLaunchRequestDTO;
import com.tusdatos.dto.client.response.JobLaunchResponseDTO;
import com.tusdatos.dto.client.response.JobReportJsonResponseDTO;
import com.tusdatos.dto.client.response.JobRetryResponseDTO;
import com.tusdatos.dto.client.response.JobStatusResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TusDatosService {

    private static final String JOB_STATUS_FINISHED = "finalizado";

    private final ApiRestClient apiRest;

    private final TusDatosProperties properties;

    /**
     * Procesa documentos mediante un flujo reactivo que incluye el lanzamiento de un trabajo,
     * la verificación de su estado, la gestión de reintentos y la generación de un informe final.
     *
     * @param launchRequestDTO El objeto de solicitud que contiene los datos necesarios para lanzar el trabajo.
     * @return Un `Mono<ReportJsonResponseDTO>` que emite el informe JSON del trabajo procesado.
     */
    public Mono<JobReportJsonResponseDTO> processDocuments(JobLaunchRequestDTO launchRequestDTO) {
        return launchJob(launchRequestDTO)
                .doOnNext(launchResponseDTO -> log.info("Processing Documents Launch"))
                .flatMap(this::jobStatus)
                .flatMap(this::retryJobStatus)
                .flatMap(this::reportJob)
                .doOnNext(reportJsonResponseDTO -> log.info("Processing Documents Report"))
                .publishOn(Schedulers.boundedElastic());
    }

    private Mono<JobLaunchResponseDTO> launchJob(JobLaunchRequestDTO launchRequestDTO) {
        return apiRest.post(properties.getEndpointJobLaunch(), launchRequestDTO, JobLaunchResponseDTO.class);
    }

    private Mono<JobStatusResponseDTO> jobStatus(JobLaunchResponseDTO launchResponseDTO) {
        var jobStatusUri = getUri(properties.getEndpointJobStatus(), launchResponseDTO.jobId());
        return pollJobStatus(jobStatusUri);
    }

    /**
     * Maneja el reintento de un trabajo con base en su estado actual.
     * Utiliza el operador `expand` para realizar reintentos recursivos hasta un máximo de 4 intentos
     * o hasta que no se requieran más reintentos.
     *
     * @param jobStatusResponseDTO El estado actual del trabajo que se está procesando.
     * @return Un `Mono<JobStatusResponseDTO>` que emite el último estado del trabajo después de los reintentos.
     */
    private Mono<JobStatusResponseDTO> retryJobStatus(JobStatusResponseDTO jobStatusResponseDTO) {
        return Mono.just(jobStatusResponseDTO)
                .expand(currentJobStatusresponseDTO ->
                        requiresRetry(
                                currentJobStatusresponseDTO)
                                ? handleRetriesIfNecessary(currentJobStatusresponseDTO)
                                : Mono.empty()
                )
                .take(properties.getNumberOfRetries())
                .last();
    }

    private Mono<JobStatusResponseDTO> retryJobStatus(JobRetryResponseDTO retryJobResponseDTO) {
        var jobStatusUri = getUri(properties.getEndpointJobStatus(), retryJobResponseDTO.jobId());
        return pollJobStatus(jobStatusUri);
    }

    /**
     * Realiza una consulta periódica al estado de un trabajo utilizando un URI específico.
     * Utiliza el operador `interval` para crear un flujo que emite eventos a intervalos regulares.
     *
     * @param statusUri El URI del estado del trabajo que se va a consultar.
     * @return Un `Mono<JobStatusResponseDTO>` que emite el último estado del trabajo consultado.
     */
    private Mono<JobStatusResponseDTO> pollJobStatus(URI statusUri) {
        return Flux.interval(properties.getJobStatusInitialDelaySeconds(), properties.getJobStatusPollingIntervalSeconds())
                .doOnNext(i -> log.debug("Polling intento #{}", i))
                .flatMap(i -> apiRest.get(statusUri.toString(), JobStatusResponseDTO.class))
                .takeUntil(response -> JOB_STATUS_FINISHED.equals(response.status()))
                .last()
                .timeout(properties.getJobStatusTimeoutSeconds())
                .doOnTerminate(() -> log.info("Job status polling finalizado"));
    }

    public Mono<JobStatusResponseDTO> handleRetriesIfNecessary(JobStatusResponseDTO jobStatusResponseDTO) {
        var retryUri = getUri(properties.getEndpointJobRetry(), jobStatusResponseDTO.id(), jobStatusResponseDTO.documentTypes());
        return apiRest.get(retryUri.toString(), JobRetryResponseDTO.class)
                .flatMap(this::retryJobStatus);
    }

    private Mono<JobReportJsonResponseDTO> reportJob(JobStatusResponseDTO jobStatusResponseDTO) {
        var reportJsonUri = getUri(properties.getEndpointReportJson(), jobStatusResponseDTO.id());
        return apiRest.get(reportJsonUri.toString(), JobReportJsonResponseDTO.class);
    }

    private URI getUri(String uri, Object... uriVariables) {
        return UriComponentsBuilder.fromUriString(uri)
                .encode()
                .buildAndExpand(uriVariables)
                .toUri();
    }


    private boolean requiresRetry(JobStatusResponseDTO status) {
        return status.error() && validateSource(status.errors());
    }

    private boolean validateSource(List<String> errors) {
        return errors.stream().anyMatch(properties.getRetryErrors()::contains);
    }

}
