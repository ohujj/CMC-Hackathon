package com.cmchackathon.domain.theater.service;

import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import com.cmchackathon.domain.theater.dto.TheaterResponse;
import com.cmchackathon.domain.theater.entity.SavedTheater;
import com.cmchackathon.domain.theater.repository.SavedTheaterRepository;
import com.cmchackathon.domain.theater.repository.TheaterRepository;
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
    private final SavedTheaterRepository savedTheaterRepository;

    public Page<TheaterResponse> getTheaters(String keyword, Pageable pageable) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        return theaterRepository.search(kw, pageable).map(TheaterResponse::from);
    }

    public TheaterResponse getTheater(String theaCd) {
        return theaterRepository.findById(theaCd)
                .map(TheaterResponse::from)
                .orElseThrow(() -> new BusinessException(ErrorCode.THEATER_NOT_FOUND));
    }

    @Transactional
    public void saveTheater(Long userId, String theaCd) {
        if (!theaterRepository.existsById(theaCd)) {
            throw new BusinessException(ErrorCode.THEATER_NOT_FOUND);
        }
        if (savedTheaterRepository.existsByIdUserIdAndIdTheaCd(userId, theaCd)) {
            return;
        }
        savedTheaterRepository.save(SavedTheater.builder()
                .userId(userId)
                .theaCd(theaCd)
                .build());
    }

    @Transactional
    public void unsaveTheater(Long userId, String theaCd) {
        savedTheaterRepository.deleteByIdUserIdAndIdTheaCd(userId, theaCd);
    }

    public Page<TheaterResponse> getSavedTheaters(Long userId, Pageable pageable) {
        return savedTheaterRepository.findSavedTheaters(userId, pageable).map(TheaterResponse::from);
    }
}
