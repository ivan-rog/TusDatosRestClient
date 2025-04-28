package com.tusdatos.bussines;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tusdatos.dto.client.enums.DocumentTypes;
import com.tusdatos.dto.client.request.JobLaunchRequestDTO;
import com.tusdatos.dto.client.response.JobReportJsonResponseDTO;
import com.tusdatos.dto.request.ValidateDocumentRequestDTO;
import com.tusdatos.mocks.JobReportJson;
import com.tusdatos.utils.JacksonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService;

    @Mock
    private TusDatosService tusDatosService;

    @Test
    @DisplayName(
            "Given a request to tus datos, when the service is called, then it should call the processDocuments " +
                    "method with the correct parameters"
    )
    @Tag("tus_datos")
    void tus_datos_success() throws JsonProcessingException {
        when(tusDatosService.processDocuments(any(JobLaunchRequestDTO.class)))
                .thenReturn(Mono.just(JacksonUtils.jsonToObject(JobReportJson.reportJsonCC111(), JobReportJsonResponseDTO.class)));
        this.documentService.launch(new ValidateDocumentRequestDTO("111", DocumentTypes.CC));
        verify(tusDatosService, times(1)).processDocuments(any(JobLaunchRequestDTO.class));
    }

    @Test
    @DisplayName(
            "Given a request to tus datos, when the service is called, then it should call the processDocuments " +
                    "method with the correct parameters and return an error"
    )
    @Tag("tus_datos")
    void tus_datos_error() {
        when(tusDatosService.processDocuments(any(JobLaunchRequestDTO.class)))
                .thenReturn(Mono.error(new RuntimeException("Error")));
        this.documentService.launch(new ValidateDocumentRequestDTO("111", DocumentTypes.CC));
        verify(tusDatosService, times(1)).processDocuments(any(JobLaunchRequestDTO.class));
    }
}