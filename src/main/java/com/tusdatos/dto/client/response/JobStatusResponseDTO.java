package com.tusdatos.dto.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tusdatos.dto.client.enums.DocumentTypes;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JobStatusResponseDTO(
        String cedula,
        boolean error,
        @JsonProperty("errores") List<String> errors,
        @JsonProperty("estado") String status,
        @JsonProperty("hallazgo") boolean finding,
        @JsonProperty("hallazgos") String findings,
        String id,
        @JsonProperty("nombre") String name,
        Object results,
        double time,
        @JsonProperty("typedoc") DocumentTypes documentTypes,
        @JsonProperty("validado") boolean validated
    ) {}
