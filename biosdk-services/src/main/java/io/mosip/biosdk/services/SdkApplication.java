package io.mosip.biosdk.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({ "${mosip.auth.adapter.impl.basepackage}", "io.mosip.biosdk.services.*"})
@SpringBootApplication()
//@EnableAutoConfiguration()
public class SdkApplication {
    public static void main(String[] args) {
        SpringApplication.run(SdkApplication.class, args);
    }
}
