package com.tusdatos;

import org.springframework.boot.SpringApplication;

public class TestTusDatosRestClientApplication {

    public static void main(String[] args) {
        SpringApplication.from(TusDatosRestClientApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
