package com.cmchackathon.domain.movie.controller;

import com.cmchackathon.global.response.ApiResponse;
import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import com.cmchackathon.domain.movie.dto.MovieDetailResponse;
import com.cmchackathon.domain.movie.dto.MovieListResponse;
import com.cmchackathon.domain.movie.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@io.swagger.v3.oas.annotations.tags.Tag(name = "02. Movie", description = "독립영화 목록 / 상세 / 포스터 프록시")
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(
            summary = "영화 목록 (페이지네이션 + 키워드 검색)",
            description = """
                    독립영화 목록을 페이지 단위로 조회한다.

                    **쿼리 파라미터**
                    - `keyword` (선택): 영화 제목 부분 일치 검색
                    - `page` (기본 0): 0부터 시작
                    - `size` (기본 20): 페이지 크기
                    - `sort` (기본 `seq,desc`): `필드명,asc|desc` 형식. 정렬 가능 필드: `seq`, `title`, `openDate`

                    **예시**
                    `GET /api/movies?keyword=밀월&page=0&size=10&sort=seq,desc`
                    """
    )
    @SecurityRequirements
    @GetMapping
    public ApiResponse<Page<MovieListResponse>> getMovies(
            @Parameter(description = "영화 제목 부분 일치 검색어", example = "밀월")
            @RequestParam(required = false) String keyword,
            @ParameterObject
            @PageableDefault(size = 20, sort = "seq", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(movieService.getMovies(keyword, pageable));
    }

    @Operation(summary = "영화 단건 상세 조회", description = "`seq`는 영화 PK")
    @SecurityRequirements
    @GetMapping("/{seq}")
    public ApiResponse<MovieDetailResponse> getMovie(
            @Parameter(description = "영화 PK", example = "1") @PathVariable Long seq
    ) {
        return ApiResponse.success(movieService.getMovie(seq));
    }

    @Operation(
            summary = "포스터 이미지 프록시",
            description = "indieground.kr 포스터 이미지를 Referer 우회하여 프록시한다. 응답: `image/jpeg` 바이너리."
    )
    @SecurityRequirements
    @GetMapping("/image/{imagePath}")
    public void proxyImage(@Parameter(description = "원본 이미지 파일명") @PathVariable String imagePath,
                           jakarta.servlet.http.HttpServletResponse response) throws Exception {
        String url = "https://indieground.kr/fileFolder/" + imagePath;
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection)
                new java.net.URL(url).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("Referer", "https://indieground.kr/indie/dbList.do");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        int status;
        try {
            status = conn.getResponseCode();
        } catch (java.io.IOException e) {
            throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
        }
        if (status < 200 || status >= 300) {
            conn.disconnect();
            throw new BusinessException(ErrorCode.MOVIE_NOT_FOUND);
        }
        response.setContentType("image/jpeg");
        try (var in = conn.getInputStream(); var out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }
}
