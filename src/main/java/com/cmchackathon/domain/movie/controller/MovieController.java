package com.cmchackathon.domain.movie.controller;

import com.cmchackathon.global.response.ApiResponse;
import com.cmchackathon.domain.movie.dto.MovieDetailResponse;
import com.cmchackathon.domain.movie.dto.MovieListResponse;
import com.cmchackathon.domain.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public ApiResponse<Page<MovieListResponse>> getMovies(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "seq", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(movieService.getMovies(keyword, pageable));
    }

    @GetMapping("/{seq}")
    public ApiResponse<MovieDetailResponse> getMovie(@PathVariable Long seq) {
        return ApiResponse.success(movieService.getMovie(seq));
    }

    @GetMapping("/image/{imagePath}")
    public void proxyImage(@PathVariable String imagePath,
                           jakarta.servlet.http.HttpServletResponse response) throws Exception {
        String url = "https://indieground.kr/fileFolder/" + imagePath;
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection)
                new java.net.URL(url).openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("Referer", "https://indieground.kr/indie/dbList.do");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        response.setContentType("image/jpeg");
        try (var in = conn.getInputStream(); var out = response.getOutputStream()) {
            in.transferTo(out);
        }
    }
}
