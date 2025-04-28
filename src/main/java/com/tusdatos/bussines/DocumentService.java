package com.tusdatos.bussines;

import com.tusdatos.dto.client.request.JobLaunchRequestDTO;
import com.tusdatos.dto.client.response.JobReportJsonResponseDTO;
import com.tusdatos.dto.request.ValidateDocumentRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final TusDatosService tusDatosService;

    public void launch(final ValidateDocumentRequestDTO validateDocument) {
        JobLaunchRequestDTO launchRequestDTO = new JobLaunchRequestDTO(
                validateDocument.documentNumber(),
                validateDocument.documentType(),
                null,
                true
        );
        this.tusDatosService.processDocuments(launchRequestDTO).
                subscribe(this::onSuccess, this::onError);
    }

    private void onSuccess(JobReportJsonResponseDTO reportJsonResponseDTO) {
        log.info("Respuesta TUS DATOS: {}", reportJsonResponseDTO.toString());
    }

    private void onError(Throwable throwable) {
        log.error("Error TUS_DATOS: {}", throwable.getMessage(), throwable);
    }

}
