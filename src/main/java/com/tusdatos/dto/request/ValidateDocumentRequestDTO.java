package com.tusdatos.dto.request;

public record ValidateDocumentRequestDTO(
        String documentNumber,
        com.tusdatos.dto.client.enums.DocumentTypes documentType
) {
}
