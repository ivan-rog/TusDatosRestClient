package com.tusdatos.dto.client.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobLaunchRequestDTO(
        @JsonProperty("doc")
        @JsonAlias("doc")
        String documentNumber,

        @JsonProperty("typedoc")
        @JsonAlias("typedoc")
        com.tusdatos.dto.client.enums.DocumentTypes documentTypes,

        @JsonProperty("fechaE")
        @JsonAlias("fechaE")
        String expirationDate,

        boolean force
) {}
