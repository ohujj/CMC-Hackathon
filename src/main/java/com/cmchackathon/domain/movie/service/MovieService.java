package com.cmchackathon.domain.movie.service;

import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import com.cmchackathon.domain.movie.dto.MovieDetailResponse;
import com.cmchackathon.domain.movie.dto.MovieListResponse;
import com.cmchackathon.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;

    public Page<MovieListResponse> getMovies(String keyword, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return movieRepository.search(kw, pageable).map(MovieListResponse::from);
    }

    public MovieDetailResponse getMovie(Long seq) {
        return movieRepository.findById(seq)
                .map(MovieDetailResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }
}
