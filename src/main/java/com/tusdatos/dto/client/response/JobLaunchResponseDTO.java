package com.tusdatos.dto.client.response;

import com.tusdatos.dto.client.enums.DocumentTypes;
import com.fasterxml.jackson.annotation.JsonProperty;

public record JobLaunchResponseDTO(
    @JsonProperty("email") String mail,
    @JsonProperty("doc") String documentNumber,
    @JsonProperty("jobid") String jobId,
    @JsonProperty("nombre") String name,
    @JsonProperty("typedoc") DocumentTypes documentType,
    @JsonProperty("validado") boolean validated
) {}
