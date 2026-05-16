package com.cmchackathon.domain.theater.controller;

import com.cmchackathon.global.response.ApiResponse;
import com.cmchackathon.domain.theater.dto.TheaterResponse;
import com.cmchackathon.domain.theater.service.TheaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "03. Theater", description = "독립영화관 목록 / 단건 / 저장 토글")
@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
public class TheaterController {

    private final TheaterService theaterService;

    @Operation(
            summary = "영화관 목록 (페이지네이션 + 키워드 검색)",
            description = """
                    독립영화관 목록을 페이지 단위로 조회한다.

                    **쿼리 파라미터**
                    - `keyword` (선택): 영화관명 부분 일치 검색
                    - `page` (기본 0), `size` (기본 20)
                    - `sort` (기본 `theaName,asc`): 정렬 가능 필드 — `theaName`, `theaCd`

                    예: `GET /api/theaters?keyword=인디&page=0&size=20&sort=theaName,asc`
                    """
    )
    @SecurityRequirements
    @GetMapping
    public ApiResponse<Page<TheaterResponse>> getTheaters(
            @Parameter(description = "영화관명 부분 일치 검색어", example = "인디")
            @RequestParam(required = false) String keyword,
            @ParameterObject
            @PageableDefault(size = 20, sort = "theaName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.success(theaterService.getTheaters(keyword, pageable));
    }

    @Operation(summary = "영화관 단건 조회")
    @SecurityRequirements
    @GetMapping("/{theaCd}")
    public ApiResponse<TheaterResponse> getTheater(
            @Parameter(description = "영화관 코드", example = "1") @PathVariable String theaCd
    ) {
        return ApiResponse.success(theaterService.getTheater(theaCd));
    }

    @Operation(summary = "영화관 저장 (북마크)")
    @PostMapping("/{theaCd}/save")
    public ApiResponse<Void> save(
            @AuthenticationPrincipal Long userId,
            @PathVariable String theaCd
    ) {
        theaterService.saveTheater(userId, theaCd);
        return ApiResponse.success(null);
    }

    @Operation(summary = "영화관 저장 취소")
    @DeleteMapping("/{theaCd}/save")
    public ApiResponse<Void> unsave(
            @AuthenticationPrincipal Long userId,
            @PathVariable String theaCd
    ) {
        theaterService.unsaveTheater(userId, theaCd);
        return ApiResponse.success(null);
    }
}
