package com.tusdatos.controller;

import com.tusdatos.bussines.DocumentService;
import com.tusdatos.dto.request.ValidateDocumentRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class TusDatosController {

    private final DocumentService documentService;

    @PostMapping(path = "/launch", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> launch(@RequestBody ValidateDocumentRequestDTO validateDocument) {
        this.documentService.launch(validateDocument);
        return ResponseEntity.accepted().build();
    }
}
