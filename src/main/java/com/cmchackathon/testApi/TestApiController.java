package com.cmchackathon.testApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestApiController {

    @GetMapping("/health")
    public ResponseEntity<String> heatlh() {

        return ResponseEntity.ok("정상 응답");
    }
}
