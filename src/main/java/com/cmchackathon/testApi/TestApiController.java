package com.cmchackathon.testApi;

import com.cmchackathon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "99. Health", description = "서버 상태 확인")
public class TestApiController {

    @Operation(summary = "헬스 체크", description = "서버 정상 동작 여부 확인 (Traefik healthcheck)")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Void>> health() {
        return ResponseEntity.ok(ApiResponse.success());
    }
}
