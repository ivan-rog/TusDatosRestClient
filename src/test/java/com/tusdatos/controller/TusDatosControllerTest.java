package com.tusdatos.controller;

import com.tusdatos.bussines.DocumentService;
import com.tusdatos.dto.client.enums.DocumentTypes;
import com.tusdatos.dto.request.ValidateDocumentRequestDTO;
import com.tusdatos.utils.JacksonUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebMvcTest(TusDatosController.class)
class TusDatosControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private DocumentService documentService;

    @Test
    @Tag("tus_datos")
    void launch_shouldReturnAccepted() {
        ValidateDocumentRequestDTO request = new ValidateDocumentRequestDTO("111", DocumentTypes.CC);
        String jsonRequest = JacksonUtils.objectToJson(request);

        mvc.post().uri("/v1/launch")
                .contentType("application/json")
                .content(jsonRequest)
                .assertThat().hasStatus2xxSuccessful();
        verify(documentService, times(1)).launch(request);
    }

}