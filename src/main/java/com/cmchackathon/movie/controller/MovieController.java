package com.cmchackathon.movie.controller;

import com.cmchackathon.global.response.ApiResponse;
import com.cmchackathon.movie.dto.MovieDetailResponse;
import com.cmchackathon.movie.dto.MovieListResponse;
import com.cmchackathon.movie.service.MovieService;
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
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String year,
            @PageableDefault(size = 20, sort = "seq", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.success(movieService.getMovies(keyword, genre, year, pageable));
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
