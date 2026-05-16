package com.cmchackathon.global;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/healthBody")
    public String healthBody() {
        return "OK";
    }

    @GetMapping("/")
    public String root() {
        return "OK";
    }
}
