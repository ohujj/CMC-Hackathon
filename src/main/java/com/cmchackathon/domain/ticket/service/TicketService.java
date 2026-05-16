package com.cmchackathon.domain.ticket.service;

import com.cmchackathon.domain.movie.entity.Movie;
import com.cmchackathon.domain.movie.repository.MovieRepository;
import com.cmchackathon.domain.ticket.dto.TicketCreateRequest;
import com.cmchackathon.domain.ticket.dto.TicketResponse;
import com.cmchackathon.domain.ticket.dto.TicketUpdateRequest;
import com.cmchackathon.domain.ticket.entity.Ticket;
import com.cmchackathon.domain.ticket.repository.TicketRepository;
import com.cmchackathon.domain.user.entity.User;
import com.cmchackathon.domain.user.repository.UserRepository;
import com.cmchackathon.global.exception.BusinessException;
import com.cmchackathon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public void createTicket(Long userId, TicketCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Movie movie = movieRepository.findById(request.getMovieSeq())
                .orElseThrow(() -> new BusinessException(ErrorCode.MOVIE_NOT_FOUND));

        Ticket ticket = Ticket.builder()
                .user(user)
                .movie(movie)
                .watchedDate(request.getWatchedDate())
                .watchedTime(request.getWatchedTime())
                .cinema(request.getCinema())
                .review(request.getReview())
                .build();

        ticketRepository.save(ticket);
    }

    public void updateTicket(Long userId, Long ticketId, TicketUpdateRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        if (!ticket.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        ticket.update(request);
    }

    public void deleteTicket(Long userId, Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        if (!ticket.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        ticket.delete();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets(Long userId) {
        return ticketRepository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicket(Long userId, Long ticketId) {
        Ticket ticket = ticketRepository.findByIdAndDeletedAtIsNull(ticketId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TICKET_NOT_FOUND));

        if (!ticket.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        return TicketResponse.from(ticket);
    }

}