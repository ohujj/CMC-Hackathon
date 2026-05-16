package com.cmchackathon.movie.service;

import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import com.cmchackathon.movie.dto.MovieDetailResponse;
import com.cmchackathon.movie.dto.MovieListResponse;
import com.cmchackathon.movie.repository.MovieRepository;
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

    public Page<MovieListResponse> getMovies(String keyword, String genre, String year, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        String g  = (genre == null || genre.isBlank()) ? null : genre.trim();
        String y  = (year == null || year.isBlank()) ? null : year.trim();
        return movieRepository.search(kw, g, y, pageable).map(MovieListResponse::from);
    }

    public MovieDetailResponse getMovie(Long seq) {
        return movieRepository.findById(seq)
                .map(MovieDetailResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }
}
