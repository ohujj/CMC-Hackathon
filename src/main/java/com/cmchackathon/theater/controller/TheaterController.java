package com.cmchackathon.theater.controller;

import com.cmchackathon.global.response.ApiResponse;
import com.cmchackathon.theater.dto.TheaterResponse;
import com.cmchackathon.theater.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/theaters")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TheaterController {

    private final TheaterService theaterService;

    @GetMapping
    public ApiResponse<Page<TheaterResponse>> getTheaters(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "theaName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ApiResponse.success(theaterService.getTheaters(keyword, pageable));
    }

    @GetMapping("/{theaCd}")
    public ApiResponse<TheaterResponse> getTheater(@PathVariable String theaCd) {
        return ApiResponse.success(theaterService.getTheater(theaCd));
    }
}
