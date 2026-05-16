package com.cmchackathon.testApi;

import com.cmchackathon.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "Health", description = "서버 상태 확인 API")
public class TestApiController {

    @Operation(summary = "GET 헬스 체크", description = "서버 정상 동작 여부 확인")
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Void>> health() {
        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "POST 헬스 체크", description = "POST요청 및 검증 확인")
    @PostMapping("/healthBody")
    public ResponseEntity<ApiResponse<Void>> healthBody(@Valid @RequestBody TestDto testDto) {

        log.info(testDto.toString());

        return ResponseEntity.ok(ApiResponse.success());

    }

    @Operation(summary = "JWT 검증 테스트")
    @GetMapping("/auth-test")
    public ResponseEntity<ApiResponse<Long>> authTest(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userId));
    }

}
