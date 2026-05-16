package com.cmchackathon.theater.service;

import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import com.cmchackathon.theater.dto.TheaterResponse;
import com.cmchackathon.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public Page<TheaterResponse> getTheaters(String keyword, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return theaterRepository.search(kw, pageable).map(TheaterResponse::from);
    }

    public TheaterResponse getTheater(String theaCd) {
        return theaterRepository.findById(theaCd)
                .map(TheaterResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    }
}
