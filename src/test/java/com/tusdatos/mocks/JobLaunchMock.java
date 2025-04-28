package com.tusdatos.mocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tusdatos.dto.client.request.JobLaunchRequestDTO;
import com.tusdatos.utils.JacksonUtils;

public class JobLaunchMock {

    private JobLaunchMock() {}

    public static JobLaunchRequestDTO launchRequestCC111() {
        try {
            return JacksonUtils.jsonToObject("""
                    {\s
                        "doc": 111,\s
                        "typedoc": "CC",\s
                        "fechaE": "01/12/2014"\s
                    }
                    """, JobLaunchRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String launchResponseCC111() {
        return """
                {
                  "email" : "usuario@pruebas.com",
                  "doc" : "111",
                  "jobid" : "6460fc34-4154-43db-9438-8c5a059304c0",
                  "nombre" : "MIGUEL FERNANDO PEREZ GOMEZ",
                  "typedoc" : "CC",
                  "validado" : true
                }
                """;
    }

    public static JobLaunchRequestDTO launchRequestNIT900000152() {
        try {
            return JacksonUtils.jsonToObject("""
                    {\s
                        "doc": 900000152,\s
                        "typedoc": "NIT"\s
                    }
                    """, JobLaunchRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
