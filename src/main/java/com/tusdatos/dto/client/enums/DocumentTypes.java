package com.tusdatos.dto.client.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DocumentTypes {
    CC("Cedula de Ciudadania"),
    CE("Cedula de Extranjeria"),
    NIT("Numero de Identificacion Tributaria"),;

    private final String description;

    DocumentTypes(final String description) {
        this.description = description;
    }

    @JsonValue
    public String getValueDocumentType() {
        return this.name();
    }
}
